package com.xrk.hws.dal.db;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.GeneratedValue;
import com.xrk.hws.dal.annotations.GenerationType;
import com.xrk.hws.dal.annotations.Id;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

@Table(name = "province_t")
public class Province
{
	@Id
	@Column(name = "id", type = DbType.BIGINT,isAutoIncre = true)
	@GeneratedValue(generationType=GenerationType.Auto)
	private long id;

	@Column(name = "name", type = DbType.VARCHAR)
	private String name;

	@Column(name = "initials", type = DbType.CHAR)
	private String initials;

	@Column(name = "is_biz", type = DbType.SMALLINT)
	private int isBiz;

	@Column(name = "order_no", type = DbType.SMALLINT)
	private int orderNo;
	
	public Province(){}
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getInitials()
	{
		return initials;
	}
	public void setInitials(String initials)
	{
		this.initials = initials;
	}
	public int getIsBiz()
	{
		return isBiz;
	}
	public void setIsBiz(int isBiz)
	{
		this.isBiz = isBiz;
	}
	public int getOrderNo()
	{
		return orderNo;
	}
	public void setOrderNo(int orderNo)
	{
		this.orderNo = orderNo;
	}
}
