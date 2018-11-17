package com.xrk.hws.common.reflection.wrapper;

import com.xrk.hws.common.reflection.MetaObject;

public interface ObjectWrapperFactory
{
	boolean hasWrapperFor(Object object);

	ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);
}
