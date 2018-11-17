package com.xrk.hws.http;

import java.util.ArrayList;
import java.util.List;

import com.xrk.hws.http.handler.HttpRequestHandler;

/**
 * 类: Http路由器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpRouter
{
	/**
	 * http路由表.
	 */
	private List<HttpRouterEntry> routers = new ArrayList<HttpRouterEntry>();
	
	/**
	 * 注册路由.  
	 *    
	 * @param method		方法名称.
	 * @param uri			请求uri.
	 * @param handler		处理器.
	 * @return
	 */
	public int registerRouterEntry(String method, String uri, HttpRequestHandler handler)
	{
		final HttpRouterEntry entry;

		try
		{
			entry = new HttpRouterEntry(method, uri, handler);
		}
		catch (IllegalArgumentException e)
		{
			return 1;
		}
		routers.add(entry);

		return 0;
	}
	
	/**
	 *  获取请求处理器.
	 *    
	 * @param method		请求方法(GET/POST).
	 * @param uri			请求url.
	 * @return				请求处理器对象.
	 */
	public HttpRequestHandler getRequestHander(String method, String uri)
	{
		for (HttpRouterEntry entry : routers)
		{
			if (entry.matchEntry(method, uri))
			{
				return entry.getHandler();
			}
		}
		return null;
	}
}
