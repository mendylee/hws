package com.xrk.hws.common.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * DefaultReflectorFactory: DefaultReflectorFactory.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DefaultReflectorFactory implements ReflectorFactory
{
	private boolean classCacheEnabled = true;
	
	private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<Class<?>, Reflector>();

	@Override
    public boolean isClassCacheEnabled()
    {
	    return classCacheEnabled;
    }

	@Override
    public void setClassCacheEnabled(boolean classCacheEnabled)
    {
		 this.classCacheEnabled = classCacheEnabled;
    }

	@Override
	public Reflector findForClass(Class<?> type)
	{
		if (classCacheEnabled) 
		{
			Reflector cached = reflectorMap.get(type);
			if (cached == null) 
			{
				cached = new Reflector(type);
				reflectorMap.put(type, cached);
			}
			return cached;
		}
		else 
		{
			return new Reflector(type);
		}
	}

}
