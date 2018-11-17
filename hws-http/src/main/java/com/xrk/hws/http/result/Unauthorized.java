package com.xrk.hws.http.result;

import static io.netty.handler.codec.http.HttpHeaders.Names.WWW_AUTHENTICATE;
import io.netty.handler.codec.http.HttpResponseStatus;

import com.xrk.hws.http.context.HttpContext;
/**
 * 类：401 Unauthorized.
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月6日
 * <br>==========================
 */
public class Unauthorized extends Result
{
    private static final long serialVersionUID = 1L;
    
	String realm;
	
    public Unauthorized(String realm) 
    {
        super(realm);
        this.realm = realm;
    }

	@Override
    public void apply(HttpContext ctx)
    {
	    ctx.response.headers().set(WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"");
		ctx.response.setStatus(HttpResponseStatus.UNAUTHORIZED);
		ctx.writeResponse();
    	return;
    }
}
