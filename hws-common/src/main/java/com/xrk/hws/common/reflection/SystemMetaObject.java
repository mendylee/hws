package com.xrk.hws.common.reflection;

import javax.naming.spi.ObjectFactory;

import com.xrk.hws.common.reflection.factory.DefaultObjectFactory;
import com.xrk.hws.common.reflection.wrapper.DefaultObjectWrapperFactory;
import com.xrk.hws.common.reflection.wrapper.ObjectWrapperFactory;



/**
 * SystemMetaObject: SystemMetaObject.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================  
 */
public class SystemMetaObject
{
	public static final ObjectFactory DEFAULT_OBJECT_FACTORY = (ObjectFactory) new DefaultObjectFactory();
	public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class,
	        DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

	private SystemMetaObject() 
	{
	}

	private static class NullObject
	{
	}

	public static MetaObject forObject(Object object)
	{
		return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
	}
}
