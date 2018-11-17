package com.xrk.hws.common.reflection.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.xrk.hws.common.reflection.ReflectionException;

/**
 * 类: 缺省对象工厂实现.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DefaultObjectFactory implements ObjectFactory, Serializable 
{
	private static final long serialVersionUID = -8855120656740914948L;

	@Override
	public <T> T create(Class<T> type)
	{
		return create(type, null, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes,
	                    List<Object> constructorArgs)
	{
		Class<?> classToCreate = resolveInterface(type);
		return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
	}

	@Override
	public void setProperties(Properties properties)
	{
	}

	<T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes,List<Object> constructorArgs)
	{
		try {
			Constructor<T> constructor;
			if (constructorArgTypes == null || constructorArgs == null) {
				constructor = type.getDeclaredConstructor();
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}
				return constructor.newInstance();
			}
			constructor = type.getDeclaredConstructor(constructorArgTypes
			        .toArray(new Class[constructorArgTypes.size()]));
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs
			        .size()]));
		}
		catch (Exception e) {
			StringBuilder argTypes = new StringBuilder();
			if (constructorArgTypes != null && !constructorArgTypes.isEmpty()) {
				for (Class<?> argType : constructorArgTypes) {
					argTypes.append(argType.getSimpleName());
					argTypes.append(",");
				}
				argTypes.deleteCharAt(argTypes.length() - 1);
			}
			StringBuilder argValues = new StringBuilder();
			if (constructorArgs != null && !constructorArgs.isEmpty()) {
				for (Object argValue : constructorArgs) {
					argValues.append(String.valueOf(argValue));
					argValues.append(",");
				}
				argValues.deleteCharAt(argValues.length() - 1);
			}
			throw new ReflectionException("Error instantiating " + type + " with invalid types ("+ argTypes + ") or values (" + argValues + "). Cause: " + e, e);
		}
	}

	protected Class<?> resolveInterface(Class<?> type)
	{
		Class<?> classToCreate;
		if (type == List.class || type == Collection.class || type == Iterable.class) {
			classToCreate = ArrayList.class;
		}
		else if (type == Map.class) {
			classToCreate = HashMap.class;
		}
		else if (type == SortedSet.class) { 
			classToCreate = TreeSet.class;
		}
		else if (type == Set.class) {
			classToCreate = HashSet.class;
		}
		else {
			classToCreate = type;
		}
		return classToCreate;
	}

	@Override
	public <T> boolean isCollection(Class<T> type)
	{
		return Collection.class.isAssignableFrom(type);
	}

}
