package com.xrk.hws.http;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSerializer;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.context.HttpContext;
import com.xrk.hws.http.handler.HttpRequestHandler;
import com.xrk.hws.http.result.BadRequest;
import com.xrk.hws.http.result.Forbidden;
import com.xrk.hws.http.result.NotFound;
import com.xrk.hws.http.result.NotModified;
import com.xrk.hws.http.result.Ok;
import com.xrk.hws.http.result.RenderJson;
import com.xrk.hws.http.result.Unauthorized;

/**
 * 类: Http处理器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@SuppressWarnings("unused")
public abstract class HttpWorkerHandler implements HttpRequestHandler
{
	/**
	 * http处理器方法/函数容器.
	 */
	private List<HttpWorkRouterEntry> lsWorkRoute = new ArrayList<HttpWorkRouterEntry>();
	
	public HttpWorkerHandler()
	{
		// 初始化route.conf文件
	}
	
	private HttpResponseStatus status;
	

    @Override
	public void handle(HttpContext ctx)
    {
		String uri = null;			
		uri = ctx.request.getUri();
		
		if(uri.lastIndexOf('/') < 0)
		{
            ctx.response.setStatus(BAD_REQUEST);
            ctx.writeResponse();
            return;
		}
		
		HttpWorkRouterEntry router = getWorkHandler(ctx.request.getMethod().name(), uri);			
		if(router != null)
		{
	        try 
	        {
	        	callFunction(router.getMethodHandler(), ctx);
	        }
	        catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException e)
	        {
	        	Logger.error(e.getMessage(), e);
	        	ctx.response.setStatus(INTERNAL_SERVER_ERROR);
	            ctx.writeResponse();	
	        }
		}
		else
		{	
			Logger.error("Not found request method. METHOD:%s, uri: %s", ctx.request.getMethod().name(), uri);
			ctx.response.setStatus(HttpResponseStatus.BAD_REQUEST);
			ctx.writeResponse();
			return;
		}
    }
    
    /**
     * 
     * 获取指定会话中URI的分组匹配数据  
     *    
     * @param ctx
     * @param uri
     * @return
     */
    protected List<String> getMatcheGroup(HttpContext ctx)
    {
    	String uri = ctx.request.getUri();
    	HttpWorkRouterEntry router = getWorkHandler(ctx.request.getMethod().name(), uri);			
		if(router != null)
		{
			return router.getMatcheGroup(uri);
		}
		return new ArrayList<String>();		
    }
    
    /**
     * 
     * 独立出调用方法，便于实现类根据需要重写  
     *    
     * @param func
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected void callFunction(Method func, HttpContext ctx) throws InvocationTargetException,IllegalAccessException
    {
    	func.invoke(this, ctx);
    }

    private HttpWorkRouterEntry getWorkHandler(String method, String uri)
    {
    	for(HttpWorkRouterEntry router : lsWorkRoute)
    	{
    		if(router.matchEntry(method, uri))
    		{
    			return router;
    		}
    	}
    	
    	return null;
    }
		
	/**
	 * 判断是否为get请求.  
	 *    
	 * @param ctx			http上下文.
	 * @return
	 */
	protected boolean isGetMethod(HttpContext ctx)
	{
		if (!ctx.request.getMethod().equals(HttpMethod.GET)) 
		{
			ctx.response.setStatus(BAD_REQUEST);
			ctx.writeResponse();
			return false;
		}
		return true;
	}
	
	/**
	 * 判断是否为post请求.  
	 *    
	 * @param ctx			http上下文.
	 * @return
	 */
	protected boolean isPostMethod(HttpContext ctx)
    {
		if (!ctx.request.getMethod().equals(HttpMethod.POST)) 
		{
			ctx.response.setStatus(BAD_REQUEST);
			ctx.writeResponse();
			return false;
		}
		return true;
    }
	
	/**
	 * 
	 * 添加http请求处理函数  
	 * <code>this.addFunction("GET","/user/\\d*", method)</code>   
	 * @param name		http请求名称
	 * @param func	http处理函数方法
	 */
	public void addFunction(String method, String uri, Method func)
	{	
		HttpWorkRouterEntry router = new HttpWorkRouterEntry(method, uri, func);
		lsWorkRoute.add(router);
	}
	
	
	public void renderJSON(HttpContext ctx,String jsonString)
	{
		RenderJson json = new RenderJson(jsonString);
		json.apply(ctx);
	}
	
	public void renderJSON(HttpContext ctx, Object o, int httpCode)
	{
		RenderJson result = new RenderJson(o, httpCode);
		result.apply(ctx);
	}
	
    /**
     * 渲染一个返回成功状态码为200，响应内容格式为application/json的响应.
     * 
     * @param ctx			Http上下文对象.
     * @param o 			序列化对象.
     */
	public void renderJSON(HttpContext ctx,Object o)
	{
		RenderJson result = new RenderJson(o);
		result.apply(ctx);
	}
	
    /**
     * 渲染一个返回成功状态码为200，响应内容格式为application/json的响应.
     * 
     * @param ctx			Http上下文对象.
     * @param o 			序列化对象.
     * @param type 			类型为复杂的泛型类型信息.
     */
    public static void renderJSON(HttpContext ctx,Object o, Type type) 
    {
		RenderJson result = new RenderJson(o, type);
		result.apply(ctx);
    }
    
    /**
     * 渲染一个返回成功状态码为200，响应内容格式为application/json的响应.
     * 
     * @param ctx			Http上下文对象.
     * @param o 			JAVA序列化对象.
     * @param adapters 		一组通过GSON序列化/反序列化/实例化创建的适配对象.
     */
    public static void renderJSON(HttpContext ctx,Object o, JsonSerializer<?>... adapters) 
    {
    	RenderJson result = new RenderJson(o, adapters);
    	result.apply(ctx);
    }
    
    /**
     * 发送一个状态码为304 Not Modified响应.
     */
    public static void notModified(HttpContext ctx) 
    {
        NotModified result =  new NotModified();
        result.apply(ctx);
    }
    
    /**
     * 发送一个响应状态码为400的错误请求.
     */
    public static void badRequest(HttpContext ctx) 
    {
    	BadRequest result =  new BadRequest();
    	result.apply(ctx);
    }
    
    /**
     * 发送一个401资源未授权的响应
     * 
     * @param realm 	域名
     */
    public static void unauthorized(HttpContext ctx,String realm) 
    {
    	Unauthorized result =  new Unauthorized(realm);
    	result.apply(ctx);
    }
    
    /**
     * 发送一个401资源未授权的响应
     */
    public static void unauthorized(HttpContext ctx) 
    {
    	Unauthorized result =  new Unauthorized("Unauthorized");
    	result.apply(ctx);
    }
    
    /**
     * 发送一个404 Not found的响应
     * 
     * @param what 	找不到资源名称
     */
	public static void notFound(HttpContext ctx, String what)
	{
		NotFound result = new NotFound(what);
		result.apply(ctx);
	}
	
    /**
     * 发送成功状态码200的响应.
     */
    public static void ok(HttpContext ctx) 
    {
    	Ok result =  new Ok();
    	result.apply(ctx);
    }
    
    /**
     * 发送一个todo的响应.表示当前不支持此操作
     */
    public static void todo(HttpContext ctx,String action)
    {
    	notFound(ctx, "This action has not been implemented Yet (" + action + ")");
    }
    
    /**
     * 发送一个403禁止响应.
     * 
     * @param reason 禁止原因.
     */
    public static void forbidden(HttpContext ctx,String reason) 
    {
    	Forbidden result =  new Forbidden(reason);
    	result.apply(ctx);
    }
    
    /**
     * 发送一个403禁止响应
     */
    public static void forbidden(HttpContext ctx) 
    {
    	Forbidden result =  new Forbidden("Access denied");
    	result.apply(ctx);
    }
    
    /**
     * 发送一个5xx错误响应.
     * 
     * @param status 状态码
     * @param reason 原因
     */
    protected static void error(HttpContext ctx,HttpResponseStatus status, String reason)
    {
    	com.xrk.hws.http.result.Error result =  new com.xrk.hws.http.result.Error(status, reason);
    	result.apply(ctx);
    }
    
    /**
     * 发送500错误响应.
     * 
     * @param reason 错误原因.
     */
    protected static void error(HttpContext ctx,String reason) 
    {
    	com.xrk.hws.http.result.Error result =  new com.xrk.hws.http.result.Error(reason);
    	result.apply(ctx);
    }
    
    /**
     * 发送500错误响应.
     * 
     * @param reason 错误原因.
     */
    protected static void error(HttpContext ctx,Exception reason) 
    {
        Logger.error(reason, "error()");
    	com.xrk.hws.http.result.Error result =  new com.xrk.hws.http.result.Error(reason.toString());
    	result.apply(ctx);
    }
    
    /**
     * 发送500错误响应.
     */
    protected static void error(HttpContext ctx) 
    {
    	com.xrk.hws.http.result.Error result =  new com.xrk.hws.http.result.Error("Internal Error");
    	result.apply(ctx);
    }
    
    /**
     * 重定向到另一个操作
     * 
     * @param action 		完全限定的操作名称 (ex: Application.index)
     * @param permanent 	true -> 301, false -> 302
     * @param args 			方法参数
     */
    protected static void redirect(HttpContext ctx,String action, boolean permanent, Object... args) 
    {
    	
    }

	/**
	 * 处理处理器方法执行前的一些事情.  
	 *    
	 * @param ctx			http上下文.
	 * @return
	 */
	protected boolean invokeBefore(HttpContext ctx)
	{
		return true;
	}
	
	/**
	 * 处理方法调用前处理的事情.  
	 *    
	 * @param ctx			http上下文.
	 * @return
	 */
	protected boolean invokeAfter(HttpContext ctx)
	{
		return true;
	}

	/**
	 * 处理一些必须收尾的事情.  
	 *    
	 * @param ctx			http上下文.
	 * @return
	 */
	protected boolean invokeFinally(HttpContext ctx)
	{
		return true;
	}
}
