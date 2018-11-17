package com.xrk.hws.dal.common;

/**
 * 类: 数据类型映射.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DbTypeMap
{
    /** 值类型 */
    private Class<?> valueType;
    
    /** 引用类型 */
    private Class<?> refType;
    
	public DbTypeMap(Class<?> valueType, Class<?> refType) 
	{
		this.setValueType(valueType);
		this.setRefType(refType);
	}

	public Class<?> getValueType()
	{
		return valueType;
	}

	public void setValueType(Class<?> valueType)
	{
		this.valueType = valueType;
	}

	public Class<?> getRefType()
	{
		return refType;
	}

	public void setRefType(Class<?> refType)
	{
		this.refType = refType;
	}
}
