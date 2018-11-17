package com.xrk.hws.dal.exception;

import com.xrk.hws.common.exceptions.PersistenceException;

/**
 * 类: 数据源异常处理类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DataSourceException extends PersistenceException
{
    private static final long serialVersionUID = 7499118087487759442L;

	public DataSourceException() 
	{
		super();
	}

	public DataSourceException(String message) 
	{
		super(message);
	}

	public DataSourceException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	public DataSourceException(Throwable cause) 
	{
		super(cause);
	}

}
