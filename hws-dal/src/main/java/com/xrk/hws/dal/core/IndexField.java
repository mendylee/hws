package com.xrk.hws.dal.core;

/**
 * 类: 索引字段.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class IndexField
{
	/**
	 * 数据字段.
	 */
	private EntityField dataField = null;
	/**
	 * 是否倒序.
	 */
	private boolean isDesc = false; 
	
    public EntityField getDataField()
	{
		return dataField;
	}
	public void setDataField(EntityField dataField)
	{
		this.dataField = dataField;
	}
	public boolean isDesc()
	{
		return isDesc;
	}
	public void setDesc(boolean isDesc)
	{
		this.isDesc = isDesc;
	}
	
}
