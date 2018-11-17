package com.xrk.hws.http.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 类: HttpChannelInitializer.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel>
{
	/**
	 * Http请求任务处理线程组，用于处理http请求任务.
	 */
	private EventExecutorGroup requestGroup = null;
	private int channelReadTimeout = 0;
	private int channelWriteTimeout = 0; 
	
	public HttpChannelInitializer(EventExecutorGroup requestGroup, int channelReadTimeout, int channelWriteTimeout)
	{
		this.requestGroup = requestGroup;
		this.channelReadTimeout = channelReadTimeout;
		this.channelWriteTimeout = channelWriteTimeout;
	}

	@Override
    protected void initChannel(SocketChannel socketChannel) throws Exception
    {
		ChannelPipeline pipeline = socketChannel.pipeline();
		
		pipeline.addLast(new ReadTimeoutHandler(this.channelReadTimeout));  
		pipeline.addLast(new WriteTimeoutHandler(this.channelWriteTimeout));
		
        pipeline.addLast("decoder", new HttpRequestDecoder());			  		
        pipeline.addLast("aggregator", new HttpObjectAggregator(1024*1024*10));	
        pipeline.addLast("encoder", new HttpResponseEncoder());			  		
        
        //如果不需要自动对内容进行压缩，将下面这一行注释
        pipeline.addLast("deflater", new HttpContentCompressor());

        if(requestGroup == null)
        {
        	pipeline.addLast("handler", new HttpHandler());
        }
        else
        {
        	pipeline.addLast(requestGroup,"handler", new HttpHandler());
        }
    }

}
