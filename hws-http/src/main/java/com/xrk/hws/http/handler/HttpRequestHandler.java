package com.xrk.hws.http.handler;

import com.xrk.hws.http.context.HttpContext;

/**
 * 接口: Http请求处理器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface HttpRequestHandler 
{
	public void handle(HttpContext ctx);
}
