package com.xrk.hws.common.reflection.wrapper;

import com.xrk.hws.common.reflection.MetaObject;
import com.xrk.hws.common.reflection.ReflectionException;

/**
 * DefaultObjectWrapperFactory: DefaultObjectWrapperFactory.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory
{
	@Override
    public boolean hasWrapperFor(Object object)
    {
	    return false;
    }

	@Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object)
    {
	    throw new ReflectionException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }

}
