package com.xrk.hws.dal.db.mc;

import com.xrk.hws.dal.common.Command;

/**
 * 
 * 类: Mc命令处理器. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MemcachedCommand extends Command
{
	/**
	 * Memcached 存储key
	 */
	private String key;
	/**
	 * Memcached 存储value
	 */
	private Object value;
	/**
	 * 是否允许重复修改
	 */
	private boolean isDupUpdate = false;
	/**
	 * 是否允许修改插入
	 */
	private boolean upInsert = false;
	/**
	 * 表达式
	 */
	private int exp = 0;

	public boolean isDupUpdate()
	{
		return isDupUpdate;
	}

	public void setDupUpdate(boolean isDupUpdate)
	{
		this.isDupUpdate = isDupUpdate;
	}

	public boolean isUpInsert()
	{
		return upInsert;
	}

	public void setUpInsert(boolean upInsert)
	{
		this.upInsert = upInsert;
	}

	public int getExp()
	{
		return exp;
	}

	public void setExp(int exp)
	{
		this.exp = exp;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
}
