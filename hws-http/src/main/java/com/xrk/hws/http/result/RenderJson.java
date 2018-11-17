package com.xrk.hws.http.result;


import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.context.HttpContext;

/**
 * RenderJson: RenderJson.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class RenderJson extends Result
{	
	private static final long serialVersionUID = 1L;

	String json;
	
	HttpResponseStatus responseCode = HttpResponseStatus.OK;
	
    public RenderJson(Object o) 
    {
        json = new Gson().toJson(o);
    }
    
    public RenderJson(Object o, int httpCode)
    {
    	json = new Gson().toJson(o);    
    	responseCode = HttpResponseStatus.valueOf(httpCode);
    }

    public RenderJson(Object o, Type type) 
    {
        json = new Gson().toJson(o, type);
    }
    
    public RenderJson(Object o, JsonSerializer<?>... adapters) 
    {
        GsonBuilder gson = new GsonBuilder();
        for (Object adapter : adapters) 
        {
            Type t = getMethod(adapter.getClass(), "serialize").getParameterTypes()[0];
            gson.registerTypeAdapter(t, adapter);
        }
        json = gson.create().toJson(o);
    }

    public RenderJson(String jsonString) 
    {
        json = jsonString;
    }

    static Method getMethod(Class<?> clazz, String name)
    {
        for (Method m : clazz.getDeclaredMethods()) 
        {
            if (m.getName().equals(name)) 
            {
                return m;
            }
        }
        return null;
    }

	@Override
    public void apply(HttpContext ctx)
    {
		ctx.response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
		//ctx.response.setStatus(HttpResponseStatus.OK);
		ctx.response.setStatus(responseCode);
		//add by shunchiguo，date:2015-6-8		
		//功能：支持jsonp返回
		//TODO:JSONP的请求参数可设置到配置表中，此处临时写上
		List<String> jsonpVal= ctx.getUriAttribute("jsonp");
		String jsonp = jsonpVal != null ? (jsonpVal.size() > 0 ? jsonpVal.get(0) : null) : null;
		if(jsonp == null)
		{
			List<String> callbackVal= ctx.getUriAttribute("callback");
			jsonp = callbackVal != null ? (callbackVal.size() > 0 ? callbackVal.get(0) : null) : null;
			//jsonp = ctx.getPostAttrValue("jsonp");
		}
		
		if(jsonp != null && !jsonp.isEmpty())
		{
			json = String.format("%s(%s)", jsonp, json);
		}
		//add end
		
		Logger.debug("hws-http response info: method=%s, uri=%s, rtnJson=%s", ctx.request.getMethod(), ctx.request.getUri(), json);
		
		ctx.response.content().writeBytes(json.getBytes());
		ctx.writeResponse();
    	return;
    }

}
