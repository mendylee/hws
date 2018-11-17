package com.xrk.hws.dal.db.mysql;

import java.util.List;

import com.xrk.hws.dal.common.Command;

/**
 * 类: Mysql操作命令.
 * 
 * <br>=
 * ========================= <br>
 * 公司：广州向日葵信息科技有限公司 <br>
 * 开发：lijp<lijingping@xiangrikui.com> <br>
 * 版本：1.0 <br>
 * 创建时间：2015年5月21日 <br>
 * JDK版本：1.7 <br>=
 * =========================
 */
public class MysqldbCommand extends Command
{
	private String sql = null;
	private String query = null;
	private String fields = null;
	private String update = null;
	private String sort = null;
	private String orderBy = null;
	private String shard = null;
	private List<String> sqls = null;
	
	private long limit = 0;
	private long offset = 0;
	private boolean isDupUpdate = false;
	private boolean upInsert = false;
	private boolean multi = false;

	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public String getFields()
	{
		return fields;
	}

	public void setFields(String fields)
	{
		this.fields = fields;
	}

	public String getUpdate()
	{
		return update;
	}

	public void setUpdate(String update)
	{
		this.update = update;
	}

	public String getSort()
	{
		return sort;
	}

	public void setSort(String sort)
	{
		this.sort = sort;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	public String getShard()
	{
		return shard;
	}

	public void setShard(String shard)
	{
		this.shard = shard;
	}

	public List<String> getSqls()
	{
		return sqls;
	}

	public void setSqls(List<String> sqls)
	{
		this.sqls = sqls;
	}

	public long getLimit()
	{
		return limit;
	}

	public void setLimit(long limit)
	{
		this.limit = limit;
	}
	
	public long getOffset()
	{
		return offset;
	}

	public void setOffset(long offset)
	{
		this.offset = offset;
	}

	public boolean isDupUpdate()
	{
		return isDupUpdate;
	}

	public void setDupUpdate(boolean isDupUpdate)
	{
		this.isDupUpdate = isDupUpdate;
	}

	public boolean isUpInsert()
	{
		return upInsert;
	}

	public void setUpInsert(boolean upInsert)
	{
		this.upInsert = upInsert;
	}

	public boolean isMulti()
	{
		return multi;
	}

	public void setMulti(boolean multi)
	{
		this.multi = multi;
	}
}
