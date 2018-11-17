package com.xrk.hws.http;

import java.util.regex.Pattern;

import com.xrk.hws.http.handler.HttpRequestHandler;

/**
 * 类: Http路由实体.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpRouterEntry
{
	/**
	 * 方法匹配模式.
	 */
	private Pattern methodPattern;
	/**
	 * URI匹配模式.
	 */
	private Pattern uriPattern;
	/**
	 * Http请求处理器接口.
	 */
	private HttpRequestHandler handler;

	/**
	 * Creates a new instance of HttpRouterEntry.  
	 *  
	 * @param method		方法名称.
	 * @param uri			请示uri
	 * @param theHandler	处理器.
	 */
	public HttpRouterEntry(String method, String uri, HttpRequestHandler theHandler) 
	{
		if ((!method.equals("GET") 
				&& !method.equals("POST") 
				&& !method.equals("PUT")
				&& !method.equals("DELETE")
				&& !method.equals(".*")) || theHandler == null) 
		{
			throw new IllegalArgumentException();
		}

		//URI不区分大小写
		methodPattern = Pattern.compile(method, Pattern.CASE_INSENSITIVE);
		uriPattern = Pattern.compile(uri, Pattern.CASE_INSENSITIVE);
		handler = theHandler;

		return;
	}

	/**
	 * 路由匹配.  
	 *    
	 * @param method	方法名称.
	 * @param uri		请示uri
	 * @return
	 */
	public boolean matchEntry(String method, String uri)
	{
		if (methodPattern.matcher(method).matches() && uriPattern.matcher(uri).matches()) 
		{
			return true;
		}
		else 
		{
			return false;
		}
	}

	/**
	 * 获取处理器.  
	 *    
	 * @return
	 */
	public HttpRequestHandler getHandler()
	{
		return handler;
	}

}
