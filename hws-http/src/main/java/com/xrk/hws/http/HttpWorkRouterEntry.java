package com.xrk.hws.http;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http工作方法类的匹配方法
 * HttpWorkRouterEntry: HttpWorkRouterEntry.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月12日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpWorkRouterEntry
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
	 * Http请求处理器方法.
	 */
	private Method methodHandler;

	/**
	 * Creates a new instance of HttpRouterEntry.  
	 *  
	 * @param method		方法名称.
	 * @param uri			请示uri
	 * @param theHandler	处理器.
	 */
	public HttpWorkRouterEntry(String method, String uri, Method theMethod) 
	{
		if ((!method.equals("GET") 
				&& !method.equals("POST") 
				&& !method.equals("PUT")
				&& !method.equals("DELETE")
				&& !method.equals(".*")) || theMethod == null) 
		{
			throw new IllegalArgumentException();
		}
		//URI判断不区分大小写
		methodPattern = Pattern.compile(method, Pattern.CASE_INSENSITIVE);
		uriPattern = Pattern.compile(uri, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		methodHandler = theMethod;
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
		if (methodPattern.matcher(method).matches() 
				&& uriPattern.matcher(uri).find()) 
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	public List<String> getMatcheGroup(String uri)
	{
		List<String> lsMatche = new ArrayList<String>(); 
		Matcher m = uriPattern.matcher(uri);
		if(m.find())
		{
			int tot = m.groupCount();
			for(int i=1; i<=tot; i++)
			{
				lsMatche.add(m.group(i));
			}
		}
		return lsMatche;
	}
	
	public String getMethod()
	{
		return methodPattern.pattern();
	}

	/**
	 * 获取处理器方法.  
	 *    
	 * @return
	 */
	public Method getMethodHandler()
	{
		return methodHandler;
	}
}
