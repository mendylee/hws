package com.xrk.hws.dist.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.xrk.hws.dist.config.ConfigContext;

/**
 * 类: 任务调度器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PoolExector
{
	private static ThreadPoolExecutor tpe;
	
	private static ScheduledThreadPoolExecutor stpe;
	
	static ThreadPoolExecutor tpe()
	{
		if (tpe == null)
		{
			int corePoolSize = ConfigContext.getInitServices();
			int maximumPoolSize = ConfigContext.getMaxServices();
			long keepAliveTime = 3000;
			TimeUnit unit = TimeUnit.MILLISECONDS;
			BlockingQueue<Runnable> waitQueue = new ArrayBlockingQueue<Runnable>(2000);
			RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();// ThreadPoolExecutor.CallerRunsPolicy();
			tpe = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,waitQueue, handler);
		}
		return tpe;
	}
}
