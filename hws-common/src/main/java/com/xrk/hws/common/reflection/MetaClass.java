package com.xrk.hws.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.xrk.hws.common.reflection.invoker.GetFieldInvoker;
import com.xrk.hws.common.reflection.invoker.Invoker;
import com.xrk.hws.common.reflection.invoker.MethodInvoker;
import com.xrk.hws.common.reflection.property.PropertyTokenizer;

/**
 * 类: 元数据类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MetaClass
{
	private ReflectorFactory reflectorFactory;
	private Reflector reflector;
	
	private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
		this.reflectorFactory = reflectorFactory;
		this.reflector = reflectorFactory.findForClass(type);
	}

	public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory)
	{
		return new MetaClass(type, reflectorFactory);
	}

	public MetaClass metaClassForProperty(String name)
	{
		Class<?> propType = reflector.getGetterType(name);
		return MetaClass.forClass(propType, reflectorFactory);
	}

	public String findProperty(String name)
	{
		StringBuilder prop = buildProperty(name, new StringBuilder());
		return prop.length() > 0 ? prop.toString() : null;
	}

	public String findProperty(String name, boolean useCamelCaseMapping)
	{
		if (useCamelCaseMapping) {
			name = name.replace("_", "");
		}
		return findProperty(name);
	}

	public String[] getGetterNames()
	{
		return reflector.getGetablePropertyNames();
	}

	public String[] getSetterNames()
	{
		return reflector.getSetablePropertyNames();
	}

	public Class<?> getSetterType(String name)
	{
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaClass metaProp = metaClassForProperty(prop.getName());
			return metaProp.getSetterType(prop.getChildren());
		}
		else {
			return reflector.getSetterType(prop.getName());
		}
	}

	public Class<?> getGetterType(String name)
	{
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaClass metaProp = metaClassForProperty(prop);
			return metaProp.getGetterType(prop.getChildren());
		}
		return getGetterType(prop);
	}

	private MetaClass metaClassForProperty(PropertyTokenizer prop)
	{
		Class<?> propType = getGetterType(prop);
		return MetaClass.forClass(propType, reflectorFactory);
	}

	private Class<?> getGetterType(PropertyTokenizer prop)
	{
		Class<?> type = reflector.getGetterType(prop.getName());
		if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
			Type returnType = getGenericGetterType(prop.getName());
			if (returnType instanceof ParameterizedType) {
				Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					returnType = actualTypeArguments[0];
					if (returnType instanceof Class) {
						type = (Class<?>) returnType;
					}
					else if (returnType instanceof ParameterizedType) {
						type = (Class<?>) ((ParameterizedType) returnType).getRawType();
					}
				}
			}
		}
		return type;
	}

	private Type getGenericGetterType(String propertyName)
	{
		try {
			Invoker invoker = reflector.getGetInvoker(propertyName);
			if (invoker instanceof MethodInvoker) {
				Field _method = MethodInvoker.class.getDeclaredField("method");
				_method.setAccessible(true);
				Method method = (Method) _method.get(invoker);
				return method.getGenericReturnType();
			}
			else if (invoker instanceof GetFieldInvoker) {
				Field _field = GetFieldInvoker.class.getDeclaredField("field");
				_field.setAccessible(true);
				Field field = (Field) _field.get(invoker);
				return field.getGenericType();
			}
		}
		catch (NoSuchFieldException e) {
		}
		catch (IllegalAccessException e) {
		}
		return null;
	}

	public boolean hasSetter(String name)
	{
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (reflector.hasSetter(prop.getName())) {
				MetaClass metaProp = metaClassForProperty(prop.getName());
				return metaProp.hasSetter(prop.getChildren());
			}
			else {
				return false;
			}
		}
		else {
			return reflector.hasSetter(prop.getName());
		}
	}

	public boolean hasGetter(String name)
	{
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (reflector.hasGetter(prop.getName())) {
				MetaClass metaProp = metaClassForProperty(prop);
				return metaProp.hasGetter(prop.getChildren());
			}
			else {
				return false;
			}
		}
		else {
			return reflector.hasGetter(prop.getName());
		}
	}

	public Invoker getGetInvoker(String name)
	{
		return reflector.getGetInvoker(name);
	}

	public Invoker getSetInvoker(String name)
	{
		return reflector.getSetInvoker(name);
	}

	private StringBuilder buildProperty(String name, StringBuilder builder)
	{
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			String propertyName = reflector.findPropertyName(prop.getName());
			if (propertyName != null) {
				builder.append(propertyName);
				builder.append(".");
				MetaClass metaProp = metaClassForProperty(propertyName);
				metaProp.buildProperty(prop.getChildren(), builder);
			}
		}
		else {
			String propertyName = reflector.findPropertyName(name);
			if (propertyName != null) {
				builder.append(propertyName);
			}
		}
		return builder;
	}

	public boolean hasDefaultConstructor()
	{
		return reflector.hasDefaultConstructor();
	}
}
