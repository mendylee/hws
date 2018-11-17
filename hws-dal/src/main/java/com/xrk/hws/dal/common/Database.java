package com.xrk.hws.dal.common;

import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.exception.DalException;

/**
 * 抽象类: 数据库.
 * 
 * <br>这是一个抽象类，提供了对数据的基本操作(CURD),它是一个物理数据存储的高度抽象类
 * <br>所有对数据存储的相关操作必须继承自它来完成对数据的基本操作
 * 
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract  class Database
{
	protected String name;
	
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
	 * 查找单条记录.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			命令模版
	 * @param parameters	查询参数
	 * @return
	 */
	public abstract  Command formatFindOneCommand(DataSet dataSet,CommandTemplate temp,Object[] parameters) throws DalException;
	
	/**
	 * 
	 * 获取序列号ID.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			命令模版
	 * @param parameters	查询参数
	 * @return
	 */
	public abstract  Command formatFindNextIdCommand(DataSet dataSet, CommandTemplate temp, Object[] queryParams,Object[] chanageParams,Object[] updateParams) throws DalException;
	
	/**
	 * 
	 * 查询多条记录.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			命令模版
	 * @param parameters	查询参数
	 * @return
	 */
	public abstract  Command formatFindMultiCommand(DataSet dataSet,CommandTemplate temp,Object[] parameters,Object[] orderbyParas)throws DalException;
	/**
	 * 
	 * 查询多条记录.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			命令模版
	 * @param parameters	查询参数
	 * @param limit			查询条目数
	 * @return
	 */
	public abstract  Command formatFindMultiCommand(DataSet dataSet,CommandTemplate temp,Object[] queryParas,Object[] orderbyParas,long limit)throws DalException;
	/**
	 * 
	 * 查询多条记录.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			命令模版
	 * @param parameters	查询参数
	 * @param offset		偏移位置
	 * @param length		查询条目数
	 * @return
	 */
	public abstract  Command formatFindMultiCommand(DataSet dataSet,CommandTemplate temp,Object[] parameters,Object[] orderbyParas,long offset,long length) throws DalException;
	
	/**
	 * 
	 * 格式化聚合查询数据模版.  
	 *    
	 * @param dataSet			数据集
	 * @param temp				命令模版
	 * @param aggregateParas	聚合参数
	 * @param opptionalParas	聚合可选项参数
	 * @return
	 */
	public abstract  Command formatFindAggregateDatasCom(DataSet dataSet, CommandTemplate temp,Object[] aggregateParas) throws DalException;
	/**
	 * 
	 * 插入记录.  
	 *    
	 * @param dataSet		数据集
	 * @param data			查询结果集
	 * @param temp			命令模版
	 * @param upDuplicated	是否允许重复插入
	 * @param extParas		扩展/额外的参数
	 * @return
	 */
	public abstract  <T> Command formatInsertOneCommand(DataSet dataSet,T data,CommandTemplate temp,boolean upDuplicated,Map<String,Object> extParas) throws DalException;
	/**
	 * 
	 * 插入多条记录.  
	 *    
	 * @param dataSet		数据集
	 * @param data			查询结果集
	 * @param temp			命令模版
	 * @param upDuplicated	是否允许重复插入
	 * @param extParas		扩展/额外的参数
	 * @return
	 */
	public abstract  <T> Command formatInsertMultiCommand(DataSet dataSet,List<T> data,CommandTemplate temp,boolean upDuplicated,Map<String,Object> extParas) throws DalException;
	/**
	 * 
	 * 删除多条记录.  
	 *    
	 * @param dataSet		数据集
	 * @param data			查询结果集
	 * @param parameters	删除条件参数
	 * @return
	 */
	public abstract  Command formatDeleteMultiCommand(DataSet dataSet,CommandTemplate temp,Object[] parameters) throws DalException;
	/**
	 * 
	 * 修改记录.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			操作命令模版
	 * @param confParas		配置参数
	 * @param upParas		修改参数
	 * @param upInsert		是否允许重复插入/修改
	 * @param multi			是否修改多条记录，true为修改多条，false为第一条符合的记录
	 * @return
	 */
	public abstract  Command formatUpdateOneCommand(DataSet dataSet,CommandTemplate temp,Object[] confParas,Object[] upParas,boolean upInsert,boolean multi) throws DalException;
	/**
	 * 
	 * 更新数据.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			操作命令模版
	 * @param confParas		配置参数
	 * @param upKVs			修改参数
	 * @param upInsert		是否允许重复插入/修改
	 * @param multi			是否修改多条记录，true为修改多条，false为第一条符合的记录
	 * @return
	 */
	public abstract  Command formatUpdateOneCommand(DataSet dataSet,CommandTemplate temp,Object[] confParas,Map<String,Object> upKVs,boolean upInsert,boolean multi) throws DalException;
	/**
	 * 
	 * 数目统计.  
	 *    
	 * @param dataSet		数据集
	 * @param temp			操作命令模版
	 * @param parameters	统计条件
	 * @return
	 */
	public abstract  Command formatCountCommand(DataSet dataSet,CommandTemplate temp,Object[] parameters) throws DalException;
	/**
	 * 
	 * 执行命令.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 */
	public abstract  void freeCommand(DataSet dataSet,Command command) throws DalException;
	/**
	 * 
	 * 这里用一句话描述这个方法的作用).  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  <T> T findOne(DataSet set,Command command) throws DalException;
	
	/**
	 * 
	 * 获取序列号ID.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract long findNextSeqenceId(DataSet set,Command command) throws DalException;
	
	/**
	 * 
	 * 获取下一个序列号ID.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract long findNextSeqId(DataSet set,Command command) throws DalException;
	/**
	 * 
	 * 查询多条记录.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  <T> List<T> findMulti(DataSet set,Command command) throws DalException;
	/**
	 * 
	 * 聚合查询.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  <T> List<T> findAggregate(DataSet set,Command command) throws DalException;
	
	
	public abstract List<DBObject> findDBObjectByAggregate(DataSet set,Command command) throws DalException;
	
	/**
	 * 
	 * 聚合查询.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  String  findAggregation(DataSet set,DBObject query)throws DalException;
	
	/**
	 * 
	 * 插入单条记录.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  int insertOne(DataSet set,Command command) throws DalException;
	/**
	 * 
	 * 批量插入记录.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  int insertMulti(DataSet set,Command command) throws DalException;
	/**
	 * 
	 * 删除多条记录.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  int deleteMulti(DataSet set,Command command) throws DalException;
	/**
	 * 
	 * 修改记录.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  int updateOne(DataSet set,Command command) throws DalException;
	/**
	 * 
	 * 数目统计.  
	 *    
	 * @param set		数据集
	 * @param command	操作命令模版
	 * @return
	 */
	public abstract  long count(DataSet set,Command command) throws DalException;
}
