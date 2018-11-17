package com.xrk.hws.dal.common;

import java.util.HashMap;

import com.xrk.hws.dal.core.Formater;

/**
 * 类: 数据操作命令参数格式化模版.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class CommandTemplate
{
	/**
	 * 操作命令模版类型
	 */
	private int type;
	/**
	 * 格式化对象字典表
	 */
	private HashMap<String, Formater> formats = new HashMap<String, Formater>();
	/**
	 * 操作命令模版名称
	 */
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public Formater getFormat(String name)
	{
		return formats.get(name);
	}

	public int addFormat(Formater format)
	{
		String name = format.getName();
		if (formats.get(name) == null)
		{
			formats.put(name, format);
			return 0;
		}
		return 1;
	}
}
