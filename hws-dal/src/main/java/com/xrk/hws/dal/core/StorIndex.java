package com.xrk.hws.dal.core;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 类: 存储索引. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class StorIndex
{
	/**
	 * 索引名称
	 */
	private String name = null;
	/**
	 * 索引字段集合
	 */
	private Set<IndexField> indexfields = new LinkedHashSet<IndexField>();
	/**
	 * 是否为主键索引
	 */
	private boolean isPk = false;
	/**
	 * 是否为唯一索引
	 */
	private boolean isUnique = false;

	public boolean isPk()
	{
		return isPk;
	}

	public void setPk(boolean isPk)
	{
		this.isPk = isPk;
	}

	public boolean isUnique()
	{
		return isUnique;
	}

	public void setUnique(boolean isUnique)
	{
		this.isUnique = isUnique;
	}

	/**
	 * 
	 * 添加索引字段  
	 *    
	 * @param field
	 * @return
	 */
	public int addIndexfield(IndexField field)
	{
		if (indexfields.add(field))
		{
			return 0;
		}

		return 1;
	}

	/**
	 * 
	 * 获取索引字段集合  
	 *    
	 * @return
	 */
	public Set<IndexField> getIndexFields()
	{
		return this.indexfields;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 
	 * 获取索引值  
	 *    
	 * @param data
	 * @return
	 */
	public Object[] getIndexValues(Object data)
	{
		int size = this.indexfields.size();
		Object[] values = new Object[size];

		int i = 0;
		for (IndexField field : this.indexfields)
		{
			values[i] = field.getDataField().getValue(data);
			if (values[i] == null)
			{
				return null;
			}
			i++;
		}

		return values;
	}

}
