package com.xrk.hws.common.reflection.wrapper;

import java.util.List;

import javax.naming.spi.ObjectFactory;

import com.xrk.hws.common.reflection.MetaObject;
import com.xrk.hws.common.reflection.property.PropertyTokenizer;

/**
 * ObjectWrapper: ObjectWrapper.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface ObjectWrapper
{
	Object get(PropertyTokenizer prop);

	void set(PropertyTokenizer prop, Object value);

	String findProperty(String name, boolean useCamelCaseMapping);

	String[] getGetterNames();

	String[] getSetterNames();

	Class<?> getSetterType(String name);

	Class<?> getGetterType(String name);

	boolean hasSetter(String name);

	boolean hasGetter(String name);

	MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop,ObjectFactory objectFactory);

	boolean isCollection();

	public void add(Object element);

	public <E> void addAll(List<E> element);
}
