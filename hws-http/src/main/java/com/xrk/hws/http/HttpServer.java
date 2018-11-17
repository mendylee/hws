package com.xrk.hws.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.http.handler.HttpChannelInitializer;
import com.xrk.hws.http.handler.HttpRequestHandler;

/**
 * 类: Http服务.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpServer
{
	/**
	 * 接收网络连接事件的工作线程.
	 */
	private EventLoopGroup bossGroup;
	/**
	 * 网络建立连接后，处理网络数据读写的工作线程.
	 */
	private EventLoopGroup workerGroup;
	/**
	 * 执行请求处理任务线程组.
	 */
	private EventExecutorGroup requestGroup = null;
	/**
	 * 服务启动器.
	 */
	private ServerBootstrap bootstrap;
	/**
	 * 监听网络信号通道流.
	 */
	private List<Channel> listenChannels;
	
	private int channelReadTimeout = 60 * 3;//默认3分钟
	private int channelWriteTimeout = 60 * 1;//默认1分钟 
	/**
	 * Http路由.
	 */
	public static HttpRouter router = new HttpRouter();
	
	public HttpServer(){
		
	}
	
	public HttpServer(int channelReadTimeout, int channelWriteTimeout){
		this.channelReadTimeout = channelReadTimeout;
		this.channelWriteTimeout = channelWriteTimeout;
	}
	
	/**
	 * 初始化服务.  
	 *    
	 * @param listeners		网络连接监听线程数.
	 * @param workers		网络读写线程数.
	 * @param requests		请求数.
	 * @param fileDataDir	文件数据存储目录.
	 * @return
	 */
	public int init(int listeners,int workers,int requests,String fileDataDir)
	{
		bossGroup 	= new NioEventLoopGroup(listeners);
		workerGroup = new NioEventLoopGroup(workers);
		listenChannels = new ArrayList<Channel>();
		
		if (requests > 0)
		{
			requestGroup = new DefaultEventExecutorGroup(requests);
		}
		
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
											   .childHandler(new HttpChannelInitializer(requestGroup, channelReadTimeout, channelWriteTimeout));
		
		//服务启动后删除已经退出的临时文件
		DiskFileUpload.deleteOnExitTemporaryFile = true;
		DiskFileUpload.baseDirectory = fileDataDir; 
		DiskAttribute.deleteOnExitTemporaryFile = true; 
		DiskAttribute.baseDirectory = fileDataDir;

		return 0;
	}
	
	/**
	 * 添加网络地址监听.  
	 *    
	 * @param address	监听网络地址.
	 * @return
	 */
	public int addListen(InetSocketAddress address)
	{
		try
		{
			Channel channel = bootstrap.bind(address).sync().channel();
			listenChannels.add(channel);
		}
		catch (InterruptedException e)
		{
			Logger.error("Bind socket address fail,[address: %s,port: %d]",address.getHostName(),address.getPort());
			return 1;
		}
		return 0;
	}
	
	public void run()
	{
		try
		{
			for (Channel ch : listenChannels)
			{
				ch.closeFuture().sync();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	/**
	 * 注册请求处理器.
	 *    
	 * @param method		请求方法.
	 * @param uri			请求uri.
	 * @param handler		请求处理器.
	 * @return
	 */
	public int registerRequestHandler(String method, String uri, HttpRequestHandler handler)
	{
		return router.registerRouterEntry(method, uri, handler);
	}
}
