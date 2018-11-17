package com.xrk.hws.common.reflection.wrapper;

import java.util.List;

import javax.naming.spi.ObjectFactory;

import com.xrk.hws.common.reflection.MetaClass;
import com.xrk.hws.common.reflection.MetaObject;
import com.xrk.hws.common.reflection.ReflectionException;
import com.xrk.hws.common.reflection.invoker.Invoker;
import com.xrk.hws.common.reflection.property.PropertyTokenizer;

/**
 * BeanWrapper: BeanWrapper.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class BeanWrapper extends BaseWrapper
{
	@SuppressWarnings("unused")
    private Object object;
	
	private MetaClass metaClass;

	public BeanWrapper(MetaObject metaObject, Object object) 
	{
		super(metaObject);
		this.object = object;
		this.metaClass = MetaClass.forClass(object.getClass(), metaObject.getReflectorFactory());
	}
	
	@Override
    public Object get(PropertyTokenizer prop)
    {
	    return null;
    }

	@Override
    public void set(PropertyTokenizer prop, Object value)
    {
	    
    }

	@Override
    public String findProperty(String name, boolean useCamelCaseMapping)
    {
	    return null;
    }

	@Override
    public String[] getGetterNames()
    {
	    return null;
    }

	@Override
    public String[] getSetterNames()
    {
	    return null;
    }

	@Override
    public Class<?> getSetterType(String name)
    {
	    return null;
    }

	@Override
    public Class<?> getGetterType(String name)
    {
	    return null;
    }

	@Override
    public boolean hasSetter(String name)
    {
	    return false;
    }

	@Override
    public boolean hasGetter(String name)
    {
	    return false;
    }

	@Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop,ObjectFactory objectFactory)
    {
	    return null;
    }

	@Override
    public boolean isCollection()
    {
	    return false;
    }

	@Override
    public void add(Object element)
    {
		throw new UnsupportedOperationException();
    }

	@Override
    public <E> void addAll(List<E> element)
    {
		throw new UnsupportedOperationException();
    }
	
	@SuppressWarnings("unused")
    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value)
	{
		try {
			Invoker method = metaClass.getSetInvoker(prop.getName());
			Object[] params = { value };
			try {
				method.invoke(object, params);
			}
			catch (Throwable t) 
			{
			}
		}
		catch (Throwable t) 
		{
			throw new ReflectionException("Could not set property '" + prop.getName() + "' of '"
			        + object.getClass() + "' with value '" + value + "' Cause: " + t.toString(), t);
		}
	}

}
