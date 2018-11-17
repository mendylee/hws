package com.xrk.hws.dal.db;

import java.util.ArrayList;
import java.util.List;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

@Table(name="test_member_entity")
public class TestMemberEntity
{
	@Column(name="i",type=DbType.SMALLINT)
	int i;
	@Column(name="j",type=DbType.SMALLINT)
	int j;
	@Column(name="k",type=DbType.COLLECTION)
	List<String> k = new ArrayList<String>();
	
	public int getI()
	{
		return i;
	}
	public void setI(int i)
	{
		this.i = i;
	}
	public int getJ()
	{
		return j;
	}
	public void setJ(int j)
	{
		this.j = j;
	}
	public List<String> getK()
	{
		return k;
	}
	public void setK(List<String> k)
	{
		this.k = k;
	}
}
