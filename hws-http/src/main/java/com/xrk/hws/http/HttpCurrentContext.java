package com.xrk.hws.http;

import com.xrk.hws.http.context.HttpContext;

/**
 * HttpCurrentContext: HttpCurrentContext.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpCurrentContext
{
	/**
	 * 用一个本地线程变量保存当前的http请求上下文
	 */
	private static ThreadLocal<HttpContext> current = new ThreadLocal<HttpContext>();
	
	
	public static void setContext(HttpContext ctx)
	{
		current.set(ctx);
	}
	
	public static void getContext()
	{
		current.get();
	}
	
	public static  void destory()
	{
		current.remove();
	}
}
