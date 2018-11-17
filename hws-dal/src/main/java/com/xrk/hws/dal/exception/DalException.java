package com.xrk.hws.dal.exception;

import com.xrk.hws.common.logger.Logger;

/**
 * 类：Dal异常类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-4-8
 * <br>==========================
 */
public class DalException extends RuntimeException
{
	private static final long serialVersionUID = -2908728641344739069L;
	private boolean isLogged = false;

	public DalException(String message) 
	{
		super(message);
	}

	public DalException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	/** 错误信息格式化封装 */
	public void logError(Exception e, String message)
	{
		// 错误信息格式
		try {
			StringBuilder error = new StringBuilder();
			error.append("\n=========================================\n");
			error.append("[Dal ERROR]\n");
			error.append(message + "\n");
			if (e != null) 
			{
				error.append("[Catch]\n");
				error.append(e.toString() + "\n");
				error.append("[Stack]\n");
				for (StackTraceElement trace : e.getStackTrace()) 
				{
					error.append("at ");
					error.append(trace.getClassName() + "." + trace.getMethodName());
					error.append("(" + trace.getFileName() + ":" + trace.getLineNumber() + ")\n");
				}
				error.append("=========================================\n");
			}
			else 
			{
				error.append("=========================================\n");
			}
			if (!this.isLogged) 
			{
				Logger.error(error.toString());
				this.isLogged = true;
			}
		}
		catch (Exception er) 
		{
			Logger.error("dal-log writting error: " + er.toString());
		}
	}
}
