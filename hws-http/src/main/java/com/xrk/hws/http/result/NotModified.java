package com.xrk.hws.http.result;

import io.netty.handler.codec.http.HttpResponseStatus;

import com.xrk.hws.http.context.HttpContext;

/**
 * 类: 类：304 Not Modified.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月30日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class NotModified extends Result
{
	private static final long serialVersionUID = 1L;
	
	String etag;

    public NotModified() 
    {
        super("NotModified");
    }

    public NotModified(String etag) 
    {
        this.etag = etag;
    }

	@Override
    public void apply(HttpContext ctx)
    {
		ctx.response.setStatus(HttpResponseStatus.NOT_MODIFIED);
		ctx.writeResponse();
    	return;
    }

}
