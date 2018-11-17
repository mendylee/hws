package com.xrk.hws.dal.core;

/**
 * 
 * 类: 操作命令参数格式化工具. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class Formater
{
	/**
	 * 需要格式化参数名称
	 */
	private String name;
	/**
	 * 需要格式的内容
	 */
	private String content;
	/**
	 * 格式化参数数量
	 */
	private int paraNum;

	public Formater(String name, String content, int paraNum)
	{
		this.setName(name);
		this.setContent(content);
		this.setParaNum(paraNum);
	}

	public int getParaNum()
	{
		return paraNum;
	}

	public void setParaNum(int paraNum)
	{
		this.paraNum = paraNum;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
}