package com.xrk.hws.http.context;


import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.NotEnoughDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.HttpCurrentContext;
import com.xrk.hws.http.handler.HttpHandler;

/**
 * 类: Http上下文.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpContext
{
	/**
	 * Netty处理器上下文.
	 */
	public ChannelHandlerContext ctx;

	/**
	 * Netty Http请求对象.
	 */
	public HttpRequest request;

	/**
	 * Netty Http响应对象.
	 */
	public FullHttpResponse response;

	/**
	 * Netty Http POST请求解码器.
	 */
	public HttpPostRequestDecoder postDecoder;

	/**
	 * Http处理器
	 */
	private HttpHandler handler;

	/**
	 * Cookie 解码
	 */
	private Set<Cookie> cookies;

	/**
	 * uri请求参数
	 */
	private Map<String, List<String>> uriAttributes;

	/**
	 * Creates a new instance of HttpContext.  
	 *  
	 * @param ctx
	 * @param request
	 * @param response
	 * @param theFront
	 * @param theDecoder
	 * @param theCookies
	 * @param theUriAttributes
	 */
	public HttpContext(ChannelHandlerContext ctx, HttpRequest request,
	        FullHttpResponse response, HttpHandler theFront, HttpPostRequestDecoder decoder,
	        Set<Cookie> cookies, Map<String, List<String>> attributes) {

		this.ctx = ctx;
		this.request = request;
		this.response = response;
		this.handler = theFront;
		this.postDecoder = decoder;
		this.cookies = cookies;
		this.uriAttributes = attributes;
	}
	
	
	/**
	 * 
	 * 写响应  
	 *    
	 * @return
	 */
	public int writeResponse()
	{		
        boolean close = HttpHeaders.Values.CLOSE.equals(request.headers().get(CONNECTION))
                || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !HttpHeaders.Values.KEEP_ALIVE.equals(request.headers().get(CONNECTION));

        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
      
        ChannelFuture future = ctx.channel().writeAndFlush(response);  
   
        handler.reset();
        HttpCurrentContext.destory();
        
        //写回数据后必须关闭连接
        if (close) 
        {
            future.addListener(ChannelFutureListener.CLOSE);
        }       
        
		return 0;
	}
	
	/**
	 * 添加私有上下文，用于请求内部传递.  
	 * <code>
	 * 	T MyContext = new T();
	 * 	//while add
	 * 	ctx.addPrivateContext(akey, MyContext);
	 * 	....
	 * 	//whiel get
	 * 	yContext = ctx.getPrivateContext(akey);
	 * </code>   
	 * @param aKey
	 * @param privateCtx
	 */
	public <T> void addPrivateContext(AttributeKey<T> key,T privateCtx)
	{
		io.netty.util.Attribute<T> attr = ctx.attr(key);
		attr.set(privateCtx);
		return;
	}
	
	/**
	 * 获取私有上下文对象.  
	 *    
	 * @param aKey
	 * @return
	 */
	public <T> T getPrivateContext(AttributeKey<T> key)
	{
		io.netty.util.Attribute<T> attr = ctx.attr(key);
		return attr.get();
	}
	
	/**
	 * 获取post属性值.  
	 *    
	 * @param paramName		参数名称.
	 * @return
	 */
	public String getPostAttrValue(String paramName)
	{
		if(postDecoder != null)
		{
			try
			{
				InterfaceHttpData mdata = postDecoder.getBodyHttpData(paramName);
			
				if (mdata != null && mdata.getHttpDataType() == HttpDataType.Attribute) 
				{
			            Attribute attribute = (Attribute) mdata;
			            try 
			            {
			                return attribute.getValue();
			            } 
			            catch (IOException e1) 
			            {	         
			    			 return null;
			            }
				 }
				
			}
			catch(NotEnoughDataDecoderException ex)
			{
				Logger.error("Not enough data decoder", ex.getMessage());
				return null;
			}
		}
		return null;
	}
	
	public Map<String,String> getPostAttrValues()
	{
		Map<String,String> postValues = null;
		
		if(postDecoder != null)
		{
			try
			{
				List<InterfaceHttpData> mdatas = postDecoder.getBodyHttpDatas();
				
				postValues = new HashMap<String,String>();
				
				for(InterfaceHttpData mdata : mdatas)
				{
					if (mdata != null && mdata.getHttpDataType() == HttpDataType.Attribute) 
					{
				            Attribute attribute = (Attribute) mdata;
				            try 
				            {
				            	String name  =  attribute.getName();
				                String value =  attribute.getValue();
				                postValues.put(name, value);
				            } 
				            catch (IOException e1) 
				            {	 
				            	 Logger.error("Get post attribute value fail.");
				    			 return null;
				            }
					 }
				}
				return postValues;
			}
			catch(NotEnoughDataDecoderException ex)
			{
				Logger.error("Not enough data decoder", ex.getMessage());
				return null;
			}
		}
		return null;
	}
	
	
	/**
	 * 获取post提交数据对象.  
	 *    
	 * @param partName	
	 * @return
	 */
	public InterfaceHttpData getPostData(String partName)
	{
		if(postDecoder != null)
		{
			try
			{
				return  postDecoder.getBodyHttpData(partName);
			}
			catch(NotEnoughDataDecoderException e1)
			{
				return null;
			}		
		}
		
		return null;		
	}
	
	/**
	 * 获取cookies字符.
	 *    
	 * @param name		cookie名称.
	 * @return
	 */
	public String getCookie(String name)
	{
		if(cookies != null)
		{
	        for (Cookie cookie : cookies) 
	        {
	        	 if(cookie.getName().equals(name))
	        	 {
	        		 return cookie.getValue();
	        	 }           
	        }
         }
        
         return null;         
	}
	
	/**
	 * 获取uri属性列表.
	 *    
	 * @param name
	 * @return
	 */
	public List<String> getUriAttribute(String name)
	{
		if(uriAttributes != null)
		{
	        for (Entry<String, List<String>> attr: uriAttributes.entrySet()) 
	        {
	        	//if(attr.getKey().equals(name))
	        	if(attr.getKey().toLowerCase().equals(name.toLowerCase()))
	        	{
	        		return attr.getValue();  	   		 
	        	}
		     }
		}
        return null;
	}
	
	/**
	 * 获取单文件上传对象.
	 *    
	 * @param name		上传文件名称.
	 * @return
	 */
	public FileUpload getFileUpload(String name)
	{
		if(postDecoder != null)
		{
			try
			{
				InterfaceHttpData data = postDecoder.getBodyHttpData(name);		
		        if ( data != null && data.getHttpDataType() == HttpDataType.FileUpload) 
		        {
		        	return (FileUpload) data;
		        }
			}
			catch(NotEnoughDataDecoderException e1)
			{
				return null;
			}
		}
        
        return null;        
	}
	
	/**
	 * 获取多文件上传列表(批量).
	 *    
	 * @param name		上传文件名称.
	 * @return
	 */
	public List<FileUpload> getFileUploads(String name)
	{
		List<FileUpload> fus = null;
		
		if(postDecoder != null)
		{
			try
			{
				List<InterfaceHttpData> datas = postDecoder.getBodyHttpDatas(name);
				if((datas != null) && (!datas.isEmpty()))
				{	
					fus = new ArrayList<FileUpload>();
					for(InterfaceHttpData data : datas)
					{
				        if ( data != null && data.getHttpDataType() == HttpDataType.FileUpload) 
				        {
				        	FileUpload fu =  (FileUpload) data;
				        	fus.add(fu);
				        }
					}
				}
			}
			catch(NotEnoughDataDecoderException e1)
			{
				return null;
			}
		}
        return fus;  
	}
}
