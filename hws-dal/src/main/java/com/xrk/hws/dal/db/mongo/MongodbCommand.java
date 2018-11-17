package com.xrk.hws.dal.db.mongo;

import java.util.List;

import com.mongodb.DBObject;
import com.xrk.hws.dal.common.Command;

/**
 * 
 * 类: Mongodb操作命令. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MongodbCommand extends Command
{
	private DBObject query = null;
	private DBObject fields = null;
	private DBObject update = null;
	private DBObject sort = null;
	private DBObject orderBy = null;
	private DBObject aggregat = null;
	private DBObject additionalOps = null;
	private DBObject change = null;
	private List<DBObject> datas = null;
	private long limit = 0;
	private boolean hasSkip = false;
	private long skip = 0;
	private boolean isDupUpdate = false;
	private boolean upInsert = false;
	private boolean multi  = false;

	public DBObject getQuery()
	{
		return query;
	}

	public void setQuery(DBObject query)
	{
		this.query = query;
	}

	public DBObject getFields()
	{
		return fields;
	}

	public void setFields(DBObject fields)
	{
		this.fields = fields;
	}

	public DBObject getUpdate()
	{
		return update;
	}

	public void setUpdate(DBObject update)
	{
		this.update = update;
	}

	public DBObject getSort()
	{
		return sort;
	}

	public void setSort(DBObject sort)
	{
		this.sort = sort;
	}

	public DBObject getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(DBObject orderBy)
	{
		this.orderBy = orderBy;
	}

	public List<DBObject> getDatas()
	{
		return datas;
	}

	public void setDatas(List<DBObject> datas)
	{
		this.datas = datas;
	}

	public long getLimit()
	{
		return limit;
	}

	public void setLimit(long limit)
	{
		this.limit = limit;
	}

	public long getSkip()
	{
		return skip;
	}

	public void setSkip(long skip)
	{
		setHasSkip(true);
		this.skip = skip;
	}

	public boolean isDupUpdate()
	{
		return isDupUpdate;
	}

	public void setDupUpdate(boolean isDupUpdate)
	{
		this.isDupUpdate = isDupUpdate;
	}

	public boolean isHasSkip()
	{
		return hasSkip;
	}

	public void setHasSkip(boolean hasSkip)
	{
		this.hasSkip = hasSkip;
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

	public DBObject getAggregat()
	{
		return aggregat;
	}

	public void setAggregat(DBObject aggregat)
	{
		this.aggregat = aggregat;
	}

	public DBObject getAdditionalOps()
	{
		return additionalOps;
	}

	public void setAdditionalOps(DBObject additionalOps)
	{
		this.additionalOps = additionalOps;
	}

	public DBObject getChange()
	{
		return change;
	}

	public void setChange(DBObject change)
	{
		this.change = change;
	}
	
}
