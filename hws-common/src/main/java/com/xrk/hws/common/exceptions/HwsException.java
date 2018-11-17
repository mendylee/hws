package com.xrk.hws.common.exceptions;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 抽象类: Hws公共异常基类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class HwsException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;

	static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());

	String id;

	public HwsException() 
	{
		super();
		setId();
	}

	public HwsException(String message) 
	{
		super(message);
		setId();
	}
	
	public HwsException(Throwable cause) 
	{
		super(cause);
	}

	public HwsException(String message, Throwable cause) 
	{
		super(message, cause);
		setId();
	}

	void setId()
	{
		long nid = atomicLong.incrementAndGet();
		id = Long.toString(nid, 26);
	}

	public abstract String getErrorTitle();

	public abstract String getErrorDescription();

	public boolean isSourceAvailable()
	{
		return this instanceof SourceAttachment;
	}

	public Integer getLineNumber()
	{
		return -1;
	}

	public String getSourceFile()
	{
		return "";
	}

	public String getId()
	{
		return id;
	}

	public String getMoreHTML()
	{
		return null;
	}
}
