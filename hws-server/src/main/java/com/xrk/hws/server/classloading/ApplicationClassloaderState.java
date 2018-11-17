package com.xrk.hws.server.classloading;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 类：应用程序类加载器状态.
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月8日
 * <br>==========================
 */
public class ApplicationClassloaderState 
{
	/**
	 * 状态值原子自增值，用于保证每一个当类加载器状态发生变化状态
	 */
    private static AtomicLong nextStateValue = new AtomicLong();

    /**
     * 当前状态值
     */
    private final long currentStateValue = nextStateValue.getAndIncrement();

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) 
        {
        	return true;
        }
        if (o == null || getClass() != o.getClass()) 
        {
        	return false;
        }
        ApplicationClassloaderState that = (ApplicationClassloaderState) o;

        if (currentStateValue != that.currentStateValue) 
        {
        	return false;
        }
        return true;
    }

    @Override
    public int hashCode() 
    {
        return (int) (currentStateValue ^ (currentStateValue >>> 32));
    }
}
