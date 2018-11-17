package com.xrk.hws.dal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xrk.hws.dal.annotations.Column;
import com.xrk.hws.dal.annotations.Index;
import com.xrk.hws.dal.annotations.Indexes;
import com.xrk.hws.dal.annotations.Table;
import com.xrk.hws.dal.common.DbType;

@Table(name="test_entity")
@Indexes(value={@Index(name = "a", value = "a",unique = true)})
public class TestEntity
{
	@Column(name="ref",type=DbType.CLASS)
	private TestMemberEntity member;
	@Column(name="a",type=DbType.INT)
	private int a;
	@Column(name="b",type=DbType.VARCHAR)
	private String b;
	@Column(name="c",type=DbType.VARCHAR)
	private String[] c;
	@Column(name="d",type=DbType.DOUBLE)
	public List<Double> d = new ArrayList<Double>(2);
	@Column(name="h",type=DbType.DOUBLE)
	private double h;
	@Column(name="i",type=DbType.DOUBLE)
	private double i;
	@Column(name="j",type=DbType.SMALLINT)
	private boolean j;
	@Column(name="m",type=DbType.COLLECTION)
	private HashMap<String,TestMemberEntity> m = new HashMap<String,TestMemberEntity>();

	public HashMap<String, TestMemberEntity> getM()
	{
		return m;
	}
	public void setM(HashMap<String, TestMemberEntity> m)
	{
		this.m = m;
	}
	public int getA()
	{
		return a;
	}
	public void setA(int a)
	{
		this.a = a;
	}
	public String getB()
	{
		return b;
	}
	public void setB(String b)
	{
		this.b = b;
	}
	public String[] getC()
	{
		return c;
	}
	public void setC(String[] c)
	{
		this.c = c;
	}
	public List<Double> getD()
	{
		return d;
	}
	public void setD(List<Double> d)
	{
		this.d = d;
	}
	public double getH()
	{
		return h;
	}
	public void setH(double h)
	{
		this.h = h;
	}
	public double getI()
	{
		return i;
	}
	public void setI(double i)
	{
		this.i = i;
	}
	public boolean isJ()
	{
		return j;
	}
	public void setJ(boolean j)
	{
		this.j = j;
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
