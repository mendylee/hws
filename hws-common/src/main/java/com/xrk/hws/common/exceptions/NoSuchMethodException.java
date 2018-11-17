package com.xrk.hws.common.exceptions;

/**
 * NoSuchMethodException: NoSuchMethodException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月2日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class NoSuchMethodException extends RuntimeException
{
	private static final long serialVersionUID = -2725364246023268766L;

	public NoSuchMethodException()
	{
		super();
	}

	public NoSuchMethodException(String msg)
	{
		super(msg);
	}
}