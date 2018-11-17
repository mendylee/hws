package com.xrk.hws.http.result;

import io.netty.handler.codec.http.HttpResponseStatus;

import com.xrk.hws.http.context.HttpContext;

/**
 * 类：500 服务器或客户端内部错误
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月6日
 * <br>==========================
 */
public class Error extends Result
{
	private static final long serialVersionUID = 1L;
	
    private HttpResponseStatus status;

    public Error(String reason) 
    {
        super(reason);
        this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }

    public Error(HttpResponseStatus status, String reason) 
    {
        super(reason);
        this.status = status;
    }

	@Override
    public void apply(HttpContext ctx)
    {
	    ctx.response.setStatus(status);
		ctx.writeResponse();
    	return;
    }

}
