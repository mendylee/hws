package com.xrk.hws.dal.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.core.PhysicDatabase;

/**
 * 
 * 抽象类: 客户端集群. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class ClustClient
{
	/**
	 * 集群服务名称
	 */
	private String name;
	
	/**
	 * 数据源名称.
	 */
	protected String type;
	/**
	 * 集群服务器缓存表
	 */
	protected HashMap<Integer, DBServer> servers = new HashMap<Integer, DBServer>();

	public abstract int toBeReady();
	
	public abstract int close();
	
	/**
	 * 
	 * 添加集群服务器  
	 *    
	 * @param server	存储服务器对象
	 * @return
	 */
	public int addServer(DBServer server)
	{
		if (servers.get(server.getSid()) == null)
		{
			servers.put(server.getSid(), server);
			return 0;
		}

		return 1;
	}

	/**
	 * 
	 * 查找单条记录	  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param params		查询参数
	 * @return			
	 */
	public Command findOne(Database database, DataSet dataSet,CommandTemplate temp, Object[] params)
	{
		return null;
	}
	
	/**
	 * 
	 * 查找多条记录  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param params		查询参数
	 * @param limit			查询条目数
	 * @param offset		记录偏移位置
	 * @param length 		记录数长度
	 * @return
	 */
	public Command findMulti(Database database, DataSet dataSet,CommandTemplate temp, Object[] queryParams,Object[] orderbyParams)
	{
		return null;
	}
	public Command findMulti(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] queryParams, Object[] orderbyParams,long limit)
	{
		return null;
	}
	public Command findMulti(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] queryParams,Object[] orderbyParams,long offset,long length)
	{
		return null;
	}
	
	/**
	 * 
	 * 获取序列号ID.  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param queryParams	查询参数
	 * @param changeParams	更改参数
	 * @param updateParams	更新参数
	 * @return
	 */
	public Command findNextSequenceId(Database database, DataSet dataSet,CommandTemplate temp, Object[] queryParams,Object[] changeParams,Object[] updateParams)
	{
		return null;
	}

	/**
	 * 
	 * 插入单条记录  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param data			插入的数据对象
	 * @param temp			操作命令模版对象	
	 * @param upDuplicated	是否允许重复插入
	 * @param extParams		额外参数
	 * @return
	 */
	public <T> Command insertOne(Database database, DataSet dataSet, T data,CommandTemplate temp, boolean upDuplicated,Map<String, Object> extParams)
	{
		return null;
	}
	
	/**
	 * 
	 * 插入多条记录  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param data			插入的数据对象
	 * @param temp			操作命令模版对象	
	 * @param upDuplicated	是否允许重复插入
	 * @param extParams		额外参数
	 * @return
	 */
	public <T> Command insertMulti(Database database, DataSet dataSet,List<T> data, CommandTemplate temp,boolean upDuplicated, Map<String, Object> extParas)
	{
		return null;
	}

	/**
	 * 
	 * 删除记录  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param params		删除条件参数
	 * @return
	 */
	public Command deleteMulti(Database database, DataSet dataSet,CommandTemplate temp, Object[] params)
	{
		return null;
	}
	
	/**
	 * 
	 * 更新数据  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param confParams	配置参数
	 * @param upParams		更新条件参数
	 * @param upInsert		是否允许重复更新
	 * @return
	 */
	public Command updateOne(Database database, DataSet dataSet,CommandTemplate temp, Object[] confParams,Object[] upParams, boolean upInsert, boolean multi)
	{
		return null;
	}
	
	/**
	 * 
	 * 更新数据   
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param confParams	配置参数
	 * @param upKVs			更新条件K/V映射表
	 * @param upInsert		是否允许重复更新
	 * @return
	 */
	public Command updateOne(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] confParams,Map<String, Object> upKVs, boolean upInsert)
	{
		return null;
	}
	
	/**
	 * 
	 * 聚合查询.  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param aggregateParas聚合查询条件
	 * @param opptionalParas聚合可选条件
	 * @return
	 */
	public Command findAggregate(Database dataBase, DataSet dataSet,CommandTemplate temp,Object[] aggregateParas)
	{
		return null;
	}
	

	/**
	 * 
	 * 查询记录数  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param temp			操作命令模版对象	
	 * @param params		查询条件参数
	 * @return
	 */
	public Command count(Database database, DataSet dataSet, CommandTemplate temp,Object[] params)
	{
		return null;
	}
	
	/**
	 * 
	 * 空操作命令，主要用于检测连接的正确性  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 */
	public void freeCommand(Database database, DataSet dataSet, Command command)
	{

	}

	/**
	 * 
	 * 查询单条记录  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public <T> T findOne(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return null;
	}
	
	/**
	 * 
	 * 获取序列号ID.  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public long findNextSequenceId(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return -1;
	}
	
	/**
	 * 
	 * 查询多条记录  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public <T> List<T> findMulti(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return null;
	}
	
	/**
	 * 
	 * 聚合查询.  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public <T> List<T> findAggregate(PhysicDatabase database, DataSet dataSet, Command command)
    {
	    return null;
    }
	
	/**
	 * 聚合查询（返回dbobject对象，由业务层处理处理数据）
	 * @param database
	 * @param dataSet
	 * @param command
	 * @return
	 */
	public List<DBObject> findDBObjectByAggregate(PhysicDatabase database, DataSet dataSet, Command command)
    {
	    return null;
    }
	
	/**
	 * 
	 * 聚合查询.  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param query			查询条件
	 * @param additionalOps	可选操作选项
	 * @return
	 */
	public String findAggregation(PhysicDatabase db,DataSet set,DBObject query,DBObject additionalOps)
	{
		return null;
	}

	/**
	 * 
	 * 插入单条数据  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public int insertOne(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return -1;
	}
	
	/**
	 * 
	 * 插入多条数据  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public int insertMulti(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return -1;
	}

	/**
	 * 
	 * 删除多条数据  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public int deleteMulti(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return -1;
	}
	
	/**
	 * 
	 * 更新数据  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public int updateOne(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return -1;
	}

	/**
	 * 
	 * 数据统计  
	 *    
	 * @param database		数据库对象	
	 * @param dataSet		数据集对象
	 * @param command		操作命令对象
	 * @return
	 */
	public long count(PhysicDatabase database, DataSet dataSet, Command command)
	{
		return -1;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	
}
