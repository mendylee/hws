package com.xrk.hws.common.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.xrk.hws.common.reflection.invoker.GetFieldInvoker;
import com.xrk.hws.common.reflection.invoker.Invoker;
import com.xrk.hws.common.reflection.invoker.MethodInvoker;
import com.xrk.hws.common.reflection.invoker.SetFieldInvoker;
import com.xrk.hws.common.reflection.property.PropertyNamer;

/**
 * 类: 反射器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class Reflector
{
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private Class<?> type;
	
	private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
	private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
	
	private Map<String, Invoker> setMethods = new HashMap<String, Invoker>();
	private Map<String, Invoker> getMethods = new HashMap<String, Invoker>();
	private Map<String, Class<?>> setTypes = new HashMap<String, Class<?>>();
	private Map<String, Class<?>> getTypes = new HashMap<String, Class<?>>();
	
	private Constructor<?> defaultConstructor;
	
	private Map<String, String> caseInsensitivePropertyMap = new HashMap<String, String>();
	
	public Reflector(Class<?> clazz) 
	{
	    type = clazz;
	    addDefaultConstructor(clazz);
	    addGetMethods(clazz);
	    addSetMethods(clazz);
	    addFields(clazz);
	    readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
	    writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
	    for (String propName : readablePropertyNames) 
	    {
	      caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
	    }
	    for (String propName : writeablePropertyNames) 
	    {
	      caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
	    }
	  }

	/**
	 * 添加默认构造函数.  
	 *    
	 * @param clazz
	 */
	private void addDefaultConstructor(Class<?> clazz)
	{
		Constructor<?>[] consts = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : consts) 
		{
			if (constructor.getParameterTypes().length == 0) 
			{
				if (canAccessPrivateMethods()) 
				{
					try 
					{
						constructor.setAccessible(true);
					}
					catch (Exception e) 
					{
					}
				}
				if (constructor.isAccessible()) 
				{
					this.defaultConstructor = constructor;
				}
			}
		}
	}
	
	private void addGetMethods(Class<?> cls)
	{
		Map<String, List<Method>> conflictingGetters = new HashMap<String, List<Method>>();
		Method[] methods = getClassMethods(cls);
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("get") && name.length() > 3) {
				if (method.getParameterTypes().length == 0) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictingGetters, name, method);
				}
			}
			else if (name.startsWith("is") && name.length() > 2) {
				if (method.getParameterTypes().length == 0) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictingGetters, name, method);
				}
			}
		}
		resolveGetterConflicts(conflictingGetters);
	}
	
	/**
	 * 解析getter方法命名冲突.  
	 *    
	 * @param conflictingGetters
	 */
	private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
	    for (String propName : conflictingGetters.keySet()) {
	      List<Method> getters = conflictingGetters.get(propName);
	      Iterator<Method> iterator = getters.iterator();
	      Method firstMethod = iterator.next();
	      if (getters.size() == 1) {
	        addGetMethod(propName, firstMethod);
	      } else {
	        Method getter = firstMethod;
	        Class<?> getterType = firstMethod.getReturnType();
	        while (iterator.hasNext()) {
	          Method method = iterator.next();
	          Class<?> methodType = method.getReturnType();
	          if (methodType.equals(getterType)) {
	            throw new ReflectionException("Illegal overloaded getter method with ambiguous type for property "
	                + propName + " in class " + firstMethod.getDeclaringClass()
	                + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
	          } else if (methodType.isAssignableFrom(getterType)) {
	        	  
	          } else if (getterType.isAssignableFrom(methodType)) {
	            getter = method;
	            getterType = methodType;
	          } else {
	            throw new ReflectionException("Illegal overloaded getter method with ambiguous type for property "
	                + propName + " in class " + firstMethod.getDeclaringClass()
	                + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
	          }
	        }
	        addGetMethod(propName, getter);
	      }
	    }
	  }
	
	/**
	 * 添加getter方法.  
	 *    
	 * @param name		方法名.
	 * @param method	getter方法.
	 */
	private void addGetMethod(String name, Method method)
	{
		if (isValidPropertyName(name)) {
			getMethods.put(name, new MethodInvoker(method));
			getTypes.put(name, method.getReturnType());
		}
	}
	
	/**
	 * 添加setter方法.  
	 *    
	 * @param cls
	 */
	private void addSetMethods(Class<?> cls)
	{
		Map<String, List<Method>> conflictingSetters = new HashMap<String, List<Method>>();
		Method[] methods = getClassMethods(cls);
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3) {
				if (method.getParameterTypes().length == 1) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictingSetters, name, method);
				}
			}
		}
		resolveSetterConflicts(conflictingSetters);
	}
	
	private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name,Method method)
	{
		List<Method> list = conflictingMethods.get(name);
		if (list == null) {
			list = new ArrayList<Method>();
			conflictingMethods.put(name, list);
		}
		list.add(method);
	}
	
	private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters)
	{
		for (String propName : conflictingSetters.keySet()) {
			List<Method> setters = conflictingSetters.get(propName);
			Method firstMethod = setters.get(0);
			if (setters.size() == 1) {
				addSetMethod(propName, firstMethod);
			}
			else {
				Class<?> expectedType = getTypes.get(propName);
				if (expectedType == null) {
			          throw new ReflectionException("Illegal overloaded setter method with ambiguous type for property "
			                  + propName + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
			                  "specification and can cause unpredicatble results.");
				}
				else {
					Iterator<Method> methods = setters.iterator();
					Method setter = null;
					while (methods.hasNext()) {
						Method method = methods.next();
						if (method.getParameterTypes().length == 1
						        && expectedType.equals(method.getParameterTypes()[0])) {
							setter = method;
							break;
						}
					}
					if (setter == null) {
				           throw new ReflectionException("Illegal overloaded setter method with ambiguous type for property "
				                   + propName + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
				                   "specification and can cause unpredicatble results.");
					}
					addSetMethod(propName, setter);
				}
			}
		}
	}
	
	private void addSetMethod(String name, Method method)
	{
		if (isValidPropertyName(name)) {
			setMethods.put(name, new MethodInvoker(method));
			setTypes.put(name, method.getParameterTypes()[0]);
		}
	}
	
	private void addFields(Class<?> clazz)
	{
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (canAccessPrivateMethods()) {
				try {
					field.setAccessible(true);
				}
				catch (Exception e) {
				}
			}
			if (field.isAccessible()) {
				if (!setMethods.containsKey(field.getName())) {
					int modifiers = field.getModifiers();
					if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
						addSetField(field);
					}
				}
				if (!getMethods.containsKey(field.getName())) {
					addGetField(field);
				}
			}
		}
		if (clazz.getSuperclass() != null) {
			addFields(clazz.getSuperclass());
		}
	}
	
	private void addSetField(Field field)
	{
		if (isValidPropertyName(field.getName())) {
			setMethods.put(field.getName(), new SetFieldInvoker(field));
			setTypes.put(field.getName(), field.getType());
		}
	}

	private void addGetField(Field field)
	{
		if (isValidPropertyName(field.getName())) {
			getMethods.put(field.getName(), new GetFieldInvoker(field));
			getTypes.put(field.getName(), field.getType());
		}
	}

	private boolean isValidPropertyName(String name)
	{
		return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
	}
	
	private Method[] getClassMethods(Class<?> cls)
	{
		Map<String, Method> uniqueMethods = new HashMap<String, Method>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
			Class<?>[] interfaces = currentClass.getInterfaces();
			for (Class<?> anInterface : interfaces) {
				addUniqueMethods(uniqueMethods, anInterface.getMethods());
			}

			currentClass = currentClass.getSuperclass();
		}

		Collection<Method> methods = uniqueMethods.values();

		return methods.toArray(new Method[methods.size()]);
	}
	
	private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods)
	{
		for (Method currentMethod : methods) {
			if (!currentMethod.isBridge()) {
				String signature = getSignature(currentMethod);
				if (!uniqueMethods.containsKey(signature)) {
					if (canAccessPrivateMethods()) {
						try {
							currentMethod.setAccessible(true);
						}
						catch (Exception e) {
						}
					}

					uniqueMethods.put(signature, currentMethod);
				}
			}
		}
	}
	
	private String getSignature(Method method)
	{
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		if (returnType != null) 
		{
			sb.append(returnType.getName()).append('#');
		}
		sb.append(method.getName());
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) 
		{
			if (i == 0) 
			{
				sb.append(':');
			}
			else 
			{
				sb.append(',');
			}
			sb.append(parameters[i].getName());
		}
		return sb.toString();
	}
	
	private static boolean canAccessPrivateMethods()
	{
		try 
		{
			SecurityManager securityManager = System.getSecurityManager();
			if (null != securityManager) 
			{
				securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
			}
		}
		catch (SecurityException e) {
			return false;
		}
		return true;
	}
	
	public Class<?> getType()
	{
		return type;
	}
	
	public Constructor<?> getDefaultConstructor()
	{
		if (defaultConstructor != null) 
		{
			return defaultConstructor;
		}
		else 
		{
			throw new ReflectionException("There is no default constructor for " + type);
		}
	}

	public boolean hasDefaultConstructor()
	{
		return defaultConstructor != null;
	}
	
	public Invoker getSetInvoker(String propertyName)
	{
		Invoker method = setMethods.get(propertyName);
		if (method == null) 
		{
			throw new ReflectionException("There is no setter for property named '" + propertyName+ "' in '" + type + "'");
		}
		return method;
	}
	
	public Invoker getGetInvoker(String propertyName)
	{
		Invoker method = getMethods.get(propertyName);
		if (method == null) 
		{
			throw new ReflectionException("There is no getter for property named '" + propertyName+ "' in '" + type + "'");
		}
		return method;
	}
	
	public Class<?> getSetterType(String propertyName)
	{
		Class<?> clazz = setTypes.get(propertyName);
		if (clazz == null) 
		{
			throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
		}
		return clazz;
	}
	
	public Class<?> getGetterType(String propertyName)
	{
		Class<?> clazz = getTypes.get(propertyName);
		if (clazz == null) 
		{
			throw new ReflectionException("There is no getter for property named '" + propertyName+ "' in '" + type + "'");
		}
		return clazz;
	}
	
	public String[] getGetablePropertyNames()
	{
		return readablePropertyNames;
	}

	public String[] getSetablePropertyNames()
	{
		return writeablePropertyNames;
	}

	public boolean hasSetter(String propertyName)
	{
		return setMethods.keySet().contains(propertyName);
	}
	
	public boolean hasGetter(String propertyName)
	{
		return getMethods.keySet().contains(propertyName);
	}

	public String findPropertyName(String name)
	{
		return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
	}
}
