package com.xrk.hws.common.exceptions;

/**
 * 类: 持久化异常处理对象.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PersistenceException extends HwsException
{
	private static final long serialVersionUID = -7537395265357977271L;

	public PersistenceException() 
	{
		super();
	}

	public PersistenceException(String message) 
	{
		super(message);
	}

	public PersistenceException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	public PersistenceException(Throwable cause) 
	{
		super(cause);
	}

	@Override
    public String getErrorTitle()
    {
	    return null;
    }

	@Override
    public String getErrorDescription()
    {
	    return null;
    }

}
