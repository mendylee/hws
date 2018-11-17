package com.xrk.hws.http.result;

import io.netty.handler.codec.http.HttpResponseStatus;

import com.xrk.hws.http.context.HttpContext;
/**
 * 类：200 OK.
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月6日
 * <br>==========================
 */
public class Ok extends Result
{
	private static final long serialVersionUID = 1L;
	
    public Ok() 
    {
        super("OK");
    }

	@Override
    public void apply(HttpContext ctx)
    {
	    ctx.response.setStatus(HttpResponseStatus.OK);
		ctx.writeResponse();
    	return;
    }

}
