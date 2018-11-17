package com.xrk.hws.http.handler;


import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.util.CharsetUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.HttpCurrentContext;
import com.xrk.hws.http.HttpServer;
import com.xrk.hws.http.context.HttpContext;

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
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest>
{
	/**
	 * Netty Http数据工厂.
	 */
	private static final HttpDataFactory dataFactory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); 
	
	/**
	 * Netty Http POST请求解码器.
	 */
	private HttpPostRequestDecoder postDecoder;
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
		if (postDecoder != null)
		{
			postDecoder.cleanFiles();
		}
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception
    {
		HttpRequestHandler handler;
		HttpMethod method;
		HttpContext httpContext;
		String uri;
		
		StopWatch sw = new StopWatch();
		sw.start();
		Logger.debug("hws-http incoming request: method=%s, uri=%s", request.getMethod(), request.getUri());
		
		// 删除没用的请求
		if (!request.getDecoderResult().isSuccess())
		{
			sw.stop();
			Logger.warn("hws-http response bad request:method=%s, uri=%s, runTime=%s", request.getMethod(), request.getUri(), sw.toString());
			sendError(ctx, BAD_REQUEST);
			return;
		}

		// 获取Http请求方法
		method = request.getMethod();
		if (method != GET && method != POST && method != PUT && method != DELETE)
		{
			sw.stop();
			Logger.warn("hws-http response not allowen method request:method=%s, uri=%s, runTime=%s", request.getMethod(), request.getUri(), sw.toString());
			sendError(ctx, METHOD_NOT_ALLOWED);
			return;
		}

		uri = request.getUri();
		// 获取请求处理器
		handler = HttpServer.router.getRequestHander(method.toString(), uri);
		if (handler == null)
		{
			sw.stop();
			Logger.warn("hws-http response not found uri:method=%s, uri=%s, runTime=%s", request.getMethod(), request.getUri(), sw.toString());
			
			sendError(ctx, NOT_FOUND);
			return;
		}

		// Cookie解码
		Set<Cookie> cookies;
		String value = request.headers().get(COOKIE);
		if (value == null)
		{
			cookies = Collections.emptySet();
		}
		else
		{
			cookies = CookieDecoder.decode(value);
		}

		QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);
		Map<String, List<String>> uriAttributes = decoderQuery.parameters();

		if (method == POST  || method == PUT || method == PATCH)
		{
			try
			{
				postDecoder = new HttpPostRequestDecoder(dataFactory, request);
			}
			catch (ErrorDataDecoderException e1)
			{
				sw.stop();
				Logger.error(e1, "hws-http response post Decoder error: method=%s, uri=%s, runTime=%s",
						request.getMethod(), request.getUri(), sw.toString());
				
				e1.printStackTrace();
				sendError(ctx, BAD_REQUEST);
				return;
			}
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

		httpContext = new HttpContext(ctx, request, response, this, postDecoder, cookies,uriAttributes);

		HttpCurrentContext.setContext(httpContext);

		handler.handle(httpContext);
		
		sw.stop();
		Logger.info("hws-http end request:method=%s, uri=%s, runTime=%s", request.getMethod(), request.getUri(), sw.toString());		
		return;
    }
	
	/**
	 * 解码复位.  
	 */
	public void reset()
	{
		if (postDecoder != null)
		{
			postDecoder.destroy();
		}
		postDecoder = null;
	}
	
	/**
	 * 发送错误消息.  
	 *    
	 * @param ctx		Netty处理器上下文.
	 * @param status	Netty响应状态.
	 */
	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,status,Unpooled.copiedBuffer("Failure: "+ status.toString()+ "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		Logger.error(cause, "hws-http exceptionCaught: error=%s", cause.getMessage());
		ctx.channel().close();
	}
}
