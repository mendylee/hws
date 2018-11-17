package com.xrk.hws.dal.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;
import com.xrk.hws.dal.common.Command;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;

/**
 * 类: 结果集.
 * <p>
 * 一个类型为T的数据对象集的操作接口，支持批量增删改查，对象成员同存储字段的映射通过annotation由反射机制完成,额外传递的参数通过列表传入,参数只支持基本数据类型和byte[],
 * 数据对象集支持使用其他的对象集作为本对象集的缓存。对于每个操作可以定义一个command
 * </p>
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DataSet 
{
	/**
	 * 扩展参数，所有支持象memcached一样的拥有失效时间
	 */
	public final static  String EXPARA_EXPTIME = "exp";
	/**
	 * 结果集名称
	 */
    protected String name;
    /**
     * 数据库对象
     */
	private Database db        = null;
	/**
	 * 数据类对象
	 */
	private Entity myClass  = null;
	/**
	 * 是否对结果集进行缓存处理
	 */
	private boolean isCache = false;
	/**
	 * 操作命令模版字典
	 */
	protected HashMap<String,CommandTemplate> cmdTemplates  = new HashMap<String,CommandTemplate>();
	
	public String getName()
    {
	    return name;
    }

	public void setName(String name)
    {
	    this.name = name;
    }
	
	public Database getDb()
    {
	    return db;
    }

	public void setDb(Database db)
    {
	    this.db = db;
    }
	
	public int addCmdTemplate(CommandTemplate template)
	{
		if(cmdTemplates.get(template.getName()) == null)
		{
			cmdTemplates.put(template.getName(),template);
			
			return 0;
		}
		
		return 1;
	}
	
	public CommandTemplate getCmdTemplate(String name)
	{
		return cmdTemplates.get(name);
	}
	
	public Entity getMyClass()
    {
	    return myClass;
    }

	public void setMyClass(Entity myClass)
    {
	    this.myClass = myClass;
    }
	
	public <T> T findOne(String comTempName, Object[] queryParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		T data = null;

		temp = getCmdTemplate(comTempName);
		command = db.formatFindOneCommand(this, temp, queryParams);
		
		if (temp != null && command != null)
		{
			data = db.findOne(this, command);
			db.freeCommand(this, command);
		}
		return data;
	}
	
	/**
	 * 
	 * 获取下一个序列ID.  
	 *    
	 * @param comTempName	模版名称
	 * @param seqFunName	序列函数名
	 * @return
	 */
	public long getNextId(String comTempName,Object[] queryParams,Object[] changeParams,Object[] updateParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		long sequenceId = -1;

		temp = getCmdTemplate(comTempName);
		command = db.formatFindNextIdCommand(this, temp, queryParams,changeParams,updateParams);
		if (temp != null && command != null)
		{
			sequenceId = db.findNextSeqId(this, command);
			db.freeCommand(this, command);
		}
		return sequenceId;
	}
	
	public <T> List<T> findMulti(String comTempName, Object[] queryParams,Object[] orderbyParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		List<T> datas = null;

		temp = getCmdTemplate(comTempName);
		command = db.formatFindMultiCommand(this, temp, queryParams,orderbyParams);
		
		if (temp != null && command != null)
		{
			datas = db.findMulti(this, command);
			db.freeCommand(this, command);
			return datas;
		}
		return null;
	}
	
	public <T> List<T> findMulti(String comTempName,Object[] queryParams,Object[] orderbyParams,long limit)
	{
		CommandTemplate temp = null;
		Command command = null;
		List<T> datas = null;
		
		temp = getCmdTemplate(comTempName);
		command = db.formatFindMultiCommand(this, temp, queryParams,orderbyParams,limit);
		
		if (temp != null && command != null)
		{
			datas = db.findMulti(this, command);
			db.freeCommand(this, command);
			return datas;
		}
		return null;
	}
	
	public <T> List<T> findMulti(String comTempName, Object[] queryParams,Object[] orderbyParams, long offset, long limit)
	{
		CommandTemplate temp = null;
		Command command = null;
		List<T> datas = null;

		temp = getCmdTemplate(comTempName);
		command = db.formatFindMultiCommand(this, temp, queryParams,orderbyParams, offset, limit);
		
		if (temp != null && command != null)
		{
			datas = db.findMulti(this, command);
			db.freeCommand(this, command);
			return datas;
		}
		return null;
	}
	
	public <T> List<T> findAggregate(String comTempName,Object[] aggreParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		List<T> datas = null;
		
		temp = getCmdTemplate(comTempName);
		command = db.formatFindAggregateDatasCom(this, temp, aggreParams);
		
		if (temp != null && command != null)
		{
			datas = db.findAggregate(this, command);
			db.freeCommand(this, command);
			return datas;
		}
		return null;
	}
	
	public String findAggregation(String comTempName,DBObject query)
	{
		CommandTemplate temp = null;
		String ret = null;
		
		temp = getCmdTemplate(comTempName);
		
		if (temp != null && query != null)
		{
			ret = db.findAggregation(this, query);
		}
		return ret;
	}
	
	public <T> int insertOne(String comTempName, T data, boolean upDuplicated, Map<String, Object> extParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		int ret = 0;

		temp = getCmdTemplate(comTempName);
		command = db.formatInsertOneCommand(this, data, temp, upDuplicated, extParams);
		if (temp != null && command != null)
		{
			ret = db.insertOne(this, command);
			db.freeCommand(this, command);
		}
		return ret;
	}
	
	public <T> int insertMulti(String comTempName, List<T> datas, boolean upDuplicated, Map<String, Object> extParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		int ret = 0;

		temp = getCmdTemplate(comTempName);
		command = db.formatInsertMultiCommand(this, datas, temp, upDuplicated, extParams);
		
		if (temp != null && command != null)
		{
			ret = db.insertMulti(this, command);
			db.freeCommand(this, command);
		}
		return ret;
	}

	public int deleteMulti(String comTempName, Object[] queryParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		int ret = 0;

		temp = getCmdTemplate(comTempName);
		command = db.formatDeleteMultiCommand(this, temp, queryParams);
		
		if (temp != null && command != null)
		{
			ret = db.deleteMulti(this, command);
			db.freeCommand(this, command);
		}
		return ret;
	}
	
	public int updateOne(String comTempName, Object[] condParams, Object[] upParams, boolean upInsert,boolean multi)
	{
		CommandTemplate temp = null;
		Command command = null;
		int ret = 0;

		temp = getCmdTemplate(comTempName);
		command = db.formatUpdateOneCommand(this, temp, condParams, upParams, upInsert,multi);
		
		if (temp != null && command != null)
		{
			ret = db.updateOne(this, command);
			db.freeCommand(this, command);
		}
		return ret;
	}
	
	public int updateOne(String comTempName, Object[] condParas, Map<String, Object> upKVs,boolean upInsert,boolean isMulti)
	{
		CommandTemplate temp = null;
		Command command = null;
		int ret = 0;

		temp = getCmdTemplate(comTempName);
		command = db.formatUpdateOneCommand(this, temp, condParas, upKVs, upInsert,isMulti);
		
		if (temp != null && command != null)
		{
			ret = db.updateOne(this, command);
			db.freeCommand(this, command);
		}
		return ret;
	}	
		
	public long count(String comTempName, Object[] queryParams)
	{
		CommandTemplate temp = null;
		Command command = null;
		long ret = 0;

		temp = getCmdTemplate(comTempName);
		command = db.formatCountCommand(this, temp, queryParams);
		if (temp != null && command != null)
		{
			ret = db.count(this, command);
			db.freeCommand(this, command);
		}
		return ret;
	}

	public boolean isCache()
	{
		return isCache;
	}

	public void setCache(boolean isCache)
	{
		this.isCache = isCache;
	}
}
