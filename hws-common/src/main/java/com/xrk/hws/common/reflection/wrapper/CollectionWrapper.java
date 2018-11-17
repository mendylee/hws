package com.xrk.hws.common.reflection.wrapper;

import java.util.Collection;
import java.util.List;

import javax.naming.spi.ObjectFactory;

import com.xrk.hws.common.reflection.MetaObject;
import com.xrk.hws.common.reflection.property.PropertyTokenizer;

/**
 * 类: 集群对象包装实现.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class CollectionWrapper implements ObjectWrapper
{
	private Collection<Object> object;

	public CollectionWrapper(MetaObject metaObject, Collection<Object> object) {
		this.object = object;
	}

	@Override
	public Object get(PropertyTokenizer prop)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(PropertyTokenizer prop, Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getGetterNames()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getSetterNames()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getSetterType(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getGetterType(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSetter(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasGetter(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCollection()
	{
		return true;
	}

	@Override
	public void add(Object element)
	{
		object.add(element);
	}

	@Override
	public <E> void addAll(List<E> element)
	{
		object.addAll(element);
	}

}
