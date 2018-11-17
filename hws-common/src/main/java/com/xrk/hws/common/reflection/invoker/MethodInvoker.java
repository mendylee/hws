package com.xrk.hws.common.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 类: 方法反射调用.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MethodInvoker implements Invoker
{
	private Class<?> type;
	
	private Method method;
	
	public MethodInvoker(Method method) 
	{
	    this.method = method;
	    if (method.getParameterTypes().length == 1) 
	    {
	      type = method.getParameterTypes()[0];
	    } 
	    else 
	    {
	      type = method.getReturnType();
	    }
	  }

	  @Override
	  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException 
	  {
	    return method.invoke(target, args);
	  }

	  @Override
	  public Class<?> getType() 
	  {
	    return type;
	  }	
}
