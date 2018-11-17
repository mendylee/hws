package com.xrk.hws.http;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xrk.hws.http.context.HttpContext;

/**
 * 类: Http服务处理器测试.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpServerHandlerTest extends HttpWorkerHandler
{
	
	// 构造函数，用于添加请求和处理函数映射表
	public HttpServerHandlerTest()
	{
		//this.addFunction("register_by_mobile", "registeMobile");
		//this.addFunction("create_user", "createUser");
		try {
	        this.addFunction("POST", "/(\\d*)/(\\d*)", this.getClass().getMethod("registeMobile", HttpContext.class));
        }
        catch (NoSuchMethodException e) {
	        e.printStackTrace();
        }
        catch (SecurityException e) {
	        e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unused")
    public void registeMobile(HttpContext ctx)
	{
		List<String> ls = this.getMatcheGroup(ctx);
		// 数据映射表，作为响应数据的封装
		Map<String,Object> dataMap = new HashMap<String,Object>();
		
		String mobile = ctx.getPostAttrValue("mobile");
		dataMap.put("mobile", mobile);
		
		ResponseEntry resEntry = new ResponseEntry();
		resEntry.setCode(20001000);
		resEntry.setMsg("成功");
		resEntry.setData(dataMap);
		
		this.renderJSON(ctx, resEntry, 201);
//		this.notFound(ctx, "Not found");
//		this.unauthorized(ctx);
//		this.ok(ctx);
	}

	public static void main(String[] args)
	{
		HttpServerHandlerTest handler = new HttpServerHandlerTest();
		HttpServer server = new HttpServer();
		server.init(3, 8, 3, null);
		server.addListen(new InetSocketAddress(8081));
		if (server.registerRequestHandler(".*", "/test/.*", handler) != 0)
		{
			System.out.println("Error while register handler");
		}
		server.run();
	}
}
