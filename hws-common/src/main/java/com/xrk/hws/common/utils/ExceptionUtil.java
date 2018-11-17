package com.xrk.hws.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 类: 异常工具处理类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ExceptionUtil
{
	private ExceptionUtil() {
	}

	public static Throwable unwrapThrowable(Throwable wrapped)
	{
		Throwable unwrapped = wrapped;
		while (true) 
		{
			if (unwrapped instanceof InvocationTargetException) 
			{
				unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
			}
			else if (unwrapped instanceof UndeclaredThrowableException) 
			{
				unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
			}
			else 
			{
				return unwrapped;
			}
		}
	}
}
