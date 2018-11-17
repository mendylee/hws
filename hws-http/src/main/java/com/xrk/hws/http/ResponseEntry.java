package com.xrk.hws.http;

/**
 * 类: 响应实体key.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ResponseEntry
{
	/**
	 * http响应状态码.
	 */
	private int status;
	
	/**
	 * 业务响应编码.
	 */
	private int code;
	
	/**
	 * 响应消息内容.
	 */
	private String msg;
	
	/**
	 * 响应数据，返回数据.
	 */
	private Object data;
	
	/**
	 * 响应时间,默认为服务器时间.
	 */
	private long time = System.currentTimeMillis();
	
	/**
	 * 总条目数.
	 */
	private int total;

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}
	
	
}
