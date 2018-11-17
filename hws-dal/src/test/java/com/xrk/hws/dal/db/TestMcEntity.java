package com.xrk.hws.dal.db;

import java.util.ArrayList;
import java.util.List;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Index;
import com.xrk.hws.dal.annotations.Indexes;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

@Indexes(value={@Index(name="key",value="a",primary=true)})
@Table(name="ec")
public class TestMcEntity
{
	@Column(name="m",type=DbType.CLASS)
	private TestMemberEntity member;
	@Column(name="a",type=DbType.SMALLINT)
	private int a;
	@Column(name="b",type=DbType.DOUBLE)
	private double b;
	@Column(name="c",type=DbType.VARCHAR)
	private String c;
	@Column(name="d",type=DbType.ARRAY)
	private String[] d;
	@Column(name="e",type=DbType.COLLECTION)
	private List<String> e = new ArrayList<String>();
	
	public int getA()
	{
		return a;
	}
	public void setA(int a)
	{
		this.a = a;
	}
	public double getB()
	{
		return b;
	}
	public void setB(double b)
	{
		this.b = b;
	}
	public String getC()
	{
		return c;
	}
	public void setC(String c)
	{
		this.c = c;
	}
	public String[] getD()
	{
		return d;
	}
	public void setD(String[] d)
	{
		this.d = d;
	}
	public List<String> getE()
	{
		return e;
	}
	public void setE(List<String> e)
	{
		this.e = e;
	}
	public TestMemberEntity getMember()
    {
	    return member;
    }
	public void setMember(TestMemberEntity member)
    {
	    this.member = member;
    }

}
