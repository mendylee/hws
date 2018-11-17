package com.xrk.hws.dal.common;

import com.xrk.hws.dal.core.DataSet;

/**
 * 抽象类: 数据操作命令. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class Command
{
	//格式化命令名称常量定义
	public final static  String COUNT_FORMAT 	 = "count";
	public final static  String QUERY_FORMAT     = "query";
	public final static  String FIELDS_FORMAT    = "fields";
	public final static  String UPDATE_FORMAT    = "update";
	public final static  String GROUPBY_FORMAT	 = "groupby";
	public final static  String HAVING_FORMAT 	 = "having";
	public final static  String ORDERBY_FORMAT   = "orderby";
	public final static  String AGGREGAT_FORMAT  = "aggregate";
	public final static  String ADDITIONAL_FORMAT= "additional";
	public final static  String CHANAGE_FORMAT	 = "change";
	public final static  String CHARD_FORMAT	 = "shard";
	/**
	 * 结果集对象
	 */
	private DataSet set;
	/**
	 * 操作命令模版对象
	 */
	private CommandTemplate temp;
	
	public DataSet getSet()
	{
		return set;
	}
	
	public void setSet(DataSet set)
	{
		this.set = set;
	}
	
	public CommandTemplate getTemp()
	{
		return temp;
	}
	
	public void setTemp(CommandTemplate temp)
	{
		this.temp = temp;
	}

}
