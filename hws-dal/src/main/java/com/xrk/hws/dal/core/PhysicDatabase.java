package com.xrk.hws.dal.core;

import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;
import com.xrk.hws.dal.common.ClustClient;
import com.xrk.hws.dal.common.Command;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;


/**
 * 类: 数据库物理存储. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class PhysicDatabase extends Database
{
	protected ClustClient client;

	@Override
	public <T> T findOne(DataSet set, Command command)
	{
		return client.findOne(this, set, command);
	}
	
	@Override
	public long findNextSeqId(DataSet set,Command command)
	{
		return client.findNextSequenceId(this, set, command);
	}

	@Override
	public <T> List<T> findMulti(DataSet set, Command command)
	{
		return client.findMulti(this, set, command);
	}
	
	@Override
    public <T> List<T> findAggregate(DataSet set, Command command)
    {
	    return client.findAggregate(this,set,command);
    }
	
	public List<DBObject> findDBObjectByAggregate(DataSet set,Command command)
	{
		return client.findDBObjectByAggregate(this, set, command);
	}

	@Override
	public int insertOne(DataSet set, Command command)
	{
		return client.insertOne(this, set, command);
	}

	@Override
	public int insertMulti(DataSet set, Command command)
	{
		return client.insertMulti(this, set, command);
	}

	@Override
	public int deleteMulti(DataSet set, Command command)
	{
		return client.deleteMulti(this, set, command);
	}

	@Override
	public int updateOne(DataSet set, Command command)
	{
		return client.updateOne(this, set, command);
	}

	@Override
	public long count(DataSet set, Command command)
	{
		return client.count(this, set, command);
	}

	@Override
	public Command formatFindOneCommand(DataSet dataSet, CommandTemplate temp, Object[] queryParams)
	{
		return client.findOne(this, dataSet, temp, queryParams);
	}
	
	@Override
    public Command formatFindNextIdCommand(DataSet dataSet, CommandTemplate temp, Object[] queryParams,Object[] chanageParams,Object[] updateParams)
    {
	    return client.findNextSequenceId(this, dataSet,temp,queryParams,chanageParams,updateParams);
    }
	
	@Override
	public Command formatFindMultiCommand(DataSet dataSet, CommandTemplate temp, Object[] queryParams,Object[] orderbyParams)
	{
		return client.findMulti(this, dataSet, temp, queryParams,orderbyParams);
	}

	@Override
	public Command formatFindMultiCommand(DataSet dataSet, CommandTemplate temp, Object[] queryParams,Object[] orderbyParams,long limit)
	{
		return client.findMulti(this, dataSet, temp, queryParams,orderbyParams,limit);
	}

	@Override
	public Command formatFindMultiCommand(DataSet dataSet, CommandTemplate temp, Object[] queryParams,Object[] orderbyParams,long offset, long length)
	{
		return client.findMulti(this, dataSet, temp, queryParams,orderbyParams, offset, length);
	}
	
	@Override
    public String findAggregation(DataSet dataSet, DBObject query)
    {
	    return client.findAggregation(this, dataSet, query,null);
    }

	@Override
	public <T> Command formatInsertOneCommand(DataSet dataSet, T data, CommandTemplate temp,boolean upDuplicated, Map<String, Object> extParas)
	{
		return client.insertOne(this, dataSet, data, temp, upDuplicated, extParas);
	}

	@Override
	public <T> Command formatInsertMultiCommand(DataSet dataSet, List<T> data, CommandTemplate temp,boolean upDuplicated, Map<String, Object> extParas)
	{
		return client.insertMulti(this, dataSet, data, temp, upDuplicated, extParas);
	}

	@Override
	public Command formatDeleteMultiCommand(DataSet dataSet, CommandTemplate temp, Object[] parameters)
	{
		return client.deleteMulti(this, dataSet, temp, parameters);
	}

	@Override
	public Command formatUpdateOneCommand(DataSet dataSet, CommandTemplate temp, Object[] confParas,
	                                   Object[] upParas, boolean upInsert,boolean multi)
	{
		return client.updateOne(this, dataSet, temp, confParas, upParas, upInsert, multi);
	}

	@Override
	public Command formatCountCommand(DataSet dataSet, CommandTemplate temp, Object[] parameters)
	{
		return client.count(this, dataSet, temp, parameters);
	}

	@Override
	public void freeCommand(DataSet dataSet, Command command)
	{
		client.freeCommand(this, dataSet, command);
	}

	@Override
	public Command formatUpdateOneCommand(DataSet dataSet, CommandTemplate temp, Object[] confParas,
	                                   Map<String, Object> upKVs, boolean upInsert,boolean multi)
	{
		return client.updateOne(this, dataSet, temp, confParas, upKVs, upInsert);
	}
	
	@Override
    public Command formatFindAggregateDatasCom(DataSet dataSet, CommandTemplate temp,Object[] aggregateParas)
    {
	    return client.findAggregate(this,dataSet,temp,aggregateParas);
    }

	@Override
    public long findNextSeqenceId(DataSet set, Command command)
    {
	    return 0;
    }
	
	public ClustClient getClient()
	{
		return client;
	}

	public void setClient(ClustClient client)
	{
		this.client = client;
	}
	
}
