package com.xrk.hws.dal.db.mongo;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.types.ObjectId;

import com.google.common.base.Strings;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;
import com.xrk.hws.dal.common.ClustClient;
import com.xrk.hws.dal.common.Command;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;
import com.xrk.hws.dal.core.CommandType;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.core.Entity;
import com.xrk.hws.dal.core.EntityField;
import com.xrk.hws.dal.core.Formater;
import com.xrk.hws.dal.core.IndexField;
import com.xrk.hws.dal.core.PhysicDatabase;
import com.xrk.hws.dal.core.StorIndex;

import net.sf.json.JSONArray;

/**
 * 类: Mongodb集群客户端. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MongodbClient extends ClustClient
{
	/**
	 * mongodb集群客户端对象
	 */
	private static MongoClient mongoClient = null;
	/**
	 * 集合操作key
	 */
	private static String SETOPER = "$set";
	
	@Override
    public synchronized int toBeReady()
    {
		List<ServerAddress> mongoServers = null;
		List<MongoCredential> mongoCreds = null;
		DBServer server = null;
		
		if(mongoClient == null)
		{
			if(mongoServers == null)
			{
				if(this.servers.isEmpty()) return 1;
				mongoServers = new ArrayList<ServerAddress>();
				Iterator<Entry<Integer, DBServer>> iter = this.servers.entrySet().iterator(); 
				try
				{
					while(iter.hasNext())
					{
						mongoCreds = new ArrayList<MongoCredential>();
						Entry<Integer, DBServer> entry = iter.next(); 
						server = entry.getValue();
						ServerAddress mongoServer = new ServerAddress(server.getHost(),server.getPort());
	                    mongoServers.add(mongoServer);
	                    if(!Strings.isNullOrEmpty(server.getUser()) && !Strings.isNullOrEmpty(server.getSchema()) && server.getPass() != null)
	                    {
							MongoCredential credential = MongoCredential.createMongoCRCredential(server.getUser(), server.getSchema(), server.getPass().toCharArray());
							mongoCreds.add(credential);
	                    }
					}
					if(this.servers.size() == 1)
					{
						if(!Strings.isNullOrEmpty(this.servers.get(0).getUser()) && !Strings.isNullOrEmpty(this.servers.get(0).getSchema()) && this.servers.get(0).getPass() != null)
						{
							MongoCredential credential = MongoCredential.createMongoCRCredential(this.servers.get(0).getUser(), this.servers.get(0).getSchema(), this.servers.get(0).getPass().toCharArray());
							mongoClient = new MongoClient(new ServerAddress(this.servers.get(0).getHost(), this.servers.get(0).getPort()),Arrays.asList(credential),getClientOptions(this.servers.get(0)));
							return 0;
						}
						mongoClient = new MongoClient(new ServerAddress(this.servers.get(0).getHost(), this.servers.get(0).getPort()),getClientOptions(this.servers.get(0)));
					}
					else
					{
						if(!Strings.isNullOrEmpty(this.servers.get(0).getUser()) && !Strings.isNullOrEmpty(this.servers.get(0).getSchema()) && this.servers.get(0).getPass() != null)
						{
							mongoClient = new MongoClient(mongoServers,mongoCreds,getClientOptions(this.servers.get(0)));
							return 0;
						}
						mongoClient = new MongoClient(mongoServers,getClientOptions(this.servers.get(0)));
					}
				}
				catch(UnknownHostException e)
				{
                    System.out.printf("Fail to build mongo server %s %d",server.getHost(),server.getPort());
                    return 1;
				}
			}			
		}
		return 0;
    }
	
	@Override
	public int close()
	{
		if(mongoClient != null)
		{
			mongoClient.close();
		}
		return 0;
	}
	
	@Override
    public <T> T findOne(PhysicDatabase db, DataSet set, Command command)
    {
		T data = null;
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		
		DBObject query = ((MongodbCommand)command).getQuery();		
		DBObject dbObject = mset.findOne(query);
		
		if(dbObject != null)
		{
			data =  this.getObject(set.getMyClass(), dbObject);			  
		}	
		return data;
    }
	
	@Override
	public long findNextSequenceId(PhysicDatabase db, DataSet set, Command command)
	{
		long seqId = -1;
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		
		DBObject query = ((MongodbCommand)command).getQuery();	
		DBObject update = ((MongodbCommand)command).getUpdate();
		DBObject dbObject = mset.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
		
		String seqName = dbObject.get("seq").toString();
		
		if(dbObject != null)
		{
			seqId =  Long.parseLong(new BigDecimal(seqName).stripTrailingZeros().toPlainString()); 
		}
		return seqId;
	}

	@Override
    public <T> List<T> findMulti(PhysicDatabase db, DataSet set, Command command)
    {
		DBCursor cursor = null;
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		
		DBObject query = ((MongodbCommand)command).getQuery();
		DBObject orderBy = ((MongodbCommand)command).getOrderBy();
		
		long limit = ((MongodbCommand)command).getLimit();
		boolean hasSkip = ((MongodbCommand)command).isHasSkip();
		long skip = ((MongodbCommand)command).getSkip();
		
		createIndex(mset,db,set);
		if(query != null)
		{
			cursor = mset.find(query);
		}
		else
		{
			cursor = mset.find();
		}
		
		if(orderBy != null)
		{
			cursor = cursor.sort(orderBy);
		}
		
		if(limit > 0)
		{
			cursor = cursor.limit((int) limit);
		}
		
		if(hasSkip && skip != 0)
		{
			cursor = cursor.skip((int) skip);
		}
		
		if(cursor.count() > 0)
		{
			List<T> result = new ArrayList<T>();
		
			try 
			{
			   while(cursor.hasNext()) 
			   {
			       @SuppressWarnings("unchecked")
                   T data = (T)this.getObject(set.getMyClass(), cursor.next());
			       if(data != null)
			       {
			    	   result.add(data);			    	   
			       }
			       else
			       {
			    	   cursor.close();
			    	   return null;
			       }
			   }			   
			   cursor.close();
			   
			   return result;
			} 
		    finally 
		    {
			   cursor.close();
			}
		}
		return null;
    }
	
	@Override
	public <T> List<T> findAggregate(PhysicDatabase db, DataSet set, Command command)
	{
		
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		
		DBObject aggregate = ((MongodbCommand)command).getAggregat();
		JSONArray jsonArr = JSONArray.fromObject(aggregate.toString());
		
		DBObject match = (DBObject)JSON.parse(jsonArr.get(0).toString());
		DBObject[] additionalOps = new DBObject[jsonArr.size()-1];
				
		for(int i= 1;i < jsonArr.size();i++)
		{
			DBObject data = (DBObject)JSON.parse(jsonArr.get(i).toString());
			additionalOps[i-1] = data;
		}
		
		AggregationOutput aggregateOut = mset.aggregate(match, additionalOps);
		Iterable<DBObject> aggIterable = aggregateOut.results();
		
		if(aggIterable != null)
		{
			List<T> result = new ArrayList<T>();
			
			Iterator<DBObject> iter = aggIterable.iterator();
			while(iter.hasNext())
			{
				@SuppressWarnings("unchecked")
				T data = (T)this.getObject(set.getMyClass(), iter.next());
				if(data != null)
				{
					result.add(data);
				}
			}
			return result;
		}
		return null;
	}
	
	@Override
	public String findAggregation(PhysicDatabase db,DataSet set,DBObject query,DBObject additionalOps)
	{
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		AggregationOutput aggregation = mset.aggregate(query, additionalOps);
		return aggregation.getCommandResult().toString();
	}
	

	@Override
	public int insertOne(PhysicDatabase db, DataSet set, Command command)
    {
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		DBObject newData = null;
		List<DBObject> datas =  ((MongodbCommand) command).getDatas();
		if(datas != null && datas.size() > 0)
		{
			Iterator<?> iter = null;
			try
			{
				DBObject data = datas.get(0);
				if(((MongodbCommand)command).isDupUpdate() == false)
				{	
					newData = new BasicDBObject();
					iter = data.keySet().iterator();
					while(iter.hasNext())
					{
						String key = (String)iter.next();
						if(data.get(key) != null)
						{
							newData.put(key, data.get(key));
						}
						else
						{
							continue;
						}
					}
					mset.insert(newData);
				}
				else
				{
					newData = new BasicDBObject();
					iter = datas.get(0).keySet().iterator();
					while(iter.hasNext())
					{
						String key = (String)iter.next();
						if(data.get(key) != null)
						{
							newData.put(key, data.get(key));
						}
						else
						{
							continue;
						}
					}
					mset.save(newData);
				}
				createIndex(mset,db,set);
			}
			catch(MongoException e)
			{
				e.printStackTrace();
				return -1;
			}
		}
	    return 1;
    }
	
	private void createIndex(DBCollection mset,Database db,DataSet set)
	{
		Entity dataClass = set.getMyClass();
		if(dataClass != null)
		{
			// 获取索引
			StorIndex uniIndex = dataClass.getIndex();
			if(uniIndex == null)
			{
				HashMap<String, StorIndex> indexes = dataClass.getIndexes();
				System.out.println(indexes.size());
				Iterator<Entry<String, StorIndex>> iter = indexes.entrySet().iterator();
				while (iter.hasNext())
				{
					Map.Entry<String, StorIndex> entry = (Map.Entry<String, StorIndex>) iter.next();
					if (entry.getValue().isUnique())
					{
						uniIndex = entry.getValue();
						break;
					}
				}
			}
			
			if (uniIndex == null)
			{
				return;
			}
			
			BasicDBObject index = new BasicDBObject();
			
			EntityField field = null;
			for (IndexField indexField : uniIndex.getIndexFields())
			{
				field = indexField.getDataField();
				index.put(field.getColumnName(), 1);
				mset.createIndex(index);
			}
		}
	}

	@Override
    public int insertMulti(PhysicDatabase db, DataSet set, Command command)
    {
	    DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		List<DBObject> datas = ((MongodbCommand)command).getDatas();
		if(datas != null && datas.size() > 0)
		{
			try
			{
				if(((MongodbCommand)command).isDupUpdate() == false)
				{
					mset.insert(datas);
				}
				else
				{
					for(DBObject data:datas)
					{
						mset.save(data);
					}
				}	
				createIndex(mset,db,set);
			}
			catch(MongoException e)
			{
				e.printStackTrace();
				return -1;
			}
		}
	    return 1;
    }

	@Override
    public int deleteMulti(PhysicDatabase db, DataSet set, Command command)
    {
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
        mset.remove(((MongodbCommand)command).getQuery());
	    return 1;
    }

	@Override
    public int updateOne(PhysicDatabase db, DataSet set, Command command)
    {
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		
		DBObject query = ((MongodbCommand)command).getQuery();
		DBObject update = ((MongodbCommand)command).getUpdate();
		boolean upsert = ((MongodbCommand)command).isUpInsert();
		boolean multi  = ((MongodbCommand)command).isMulti();
		mset.update(query, update, upsert,multi);
		return 1;
	
    }

	@Override
    public long count(PhysicDatabase db, DataSet set, Command command)
    {	
		DB mdb = mongoClient.getDB(db.getName());
		DBCollection mset = mdb.getCollection(set.getName());
		DBObject query = ((MongodbCommand)command).getQuery();
		
		return (query == null) ? mset.count() : mset.count(query);
    }

	@Override
    public Command findOne(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] parameters)
    {
		MongodbCommand com = null;
		if(temp.getType() == CommandType.FINDONE)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	Formater fieldsByFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	
	    	DBObject query = this.formatPrintable(queryFormat, parameters);
	    	DBObject orderBy = this.formatPrintable(orderByFormat, null);
	    	DBObject fieldses = this.formatPrintable(fieldsByFormat, null);
	    	
			com = this.getCommand(dataBase, dataSet);
			if(com != null)
			{
				com.setTemp(temp);
				com.setQuery(query);
				com.setOrderBy(orderBy);
				com.setFields(fieldses);
			}
		}
	    return com;
    }
	
	@Override
	public Command findNextSequenceId(Database database, DataSet dataSet,CommandTemplate temp, Object[] queryParams,Object[] changeParams,Object[] updateParams)
	{
		MongodbCommand com = null;
		
		if(temp.getType() == CommandType.SEQUENCE)
		{
	    	Formater queryFormat 	= temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat 	= temp.getFormat(Command.ORDERBY_FORMAT);
	    	Formater fieldsByFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	Formater changeFormat 	= temp.getFormat(Command.CHANAGE_FORMAT);
	    	Formater updateFormat	= temp.getFormat(Command.UPDATE_FORMAT);
	    	
	    	DBObject query 	 = this.formatPrintable(queryFormat, queryParams);
	    	DBObject orderBy = this.formatPrintable(orderByFormat, null);
	    	DBObject fieldses = this.formatPrintable(fieldsByFormat, null);
	    	DBObject change = this.formatPrintable(changeFormat, changeParams);
	    	DBObject update  = this.formatPrintable(updateFormat, updateParams);
	    	
    		com = this.getCommand(database, dataSet);
			if(com != null)
			{
				com.setTemp(temp);
				com.setQuery(query);
				com.setOrderBy(orderBy);
				com.setFields(fieldses);
				com.setChange(change);
				com.setUpdate(update);
			}
		}
		return com;
	}
	
	
	@Override
    public Command findMulti(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] queryParams,Object[] orderbyParams)
    {
		MongodbCommand com = null;
		
		if(temp.getType() == CommandType.MFIND)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	DBObject query = this.formatPrintable(queryFormat, queryParams);
	    	DBObject orderBy = this.formatPrintable(orderByFormat, orderbyParams);
	    	
			com = this.getCommand(dataBase, dataSet);
			if(com != null)
			{
				com.setTemp(temp);
				com.setQuery(query);
				com.setOrderBy(orderBy);
			}
		}
	    return com;
    }

	@Override
	public Command findMulti(Database dataBase, DataSet dataSet, CommandTemplate temp,Object[] queryParams,Object[] orbderbyParams,long limit)
	{
		MongodbCommand com = null;
		
		if (temp.getType() == CommandType.MFIND)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
			
			DBObject query = this.formatPrintable(queryFormat, queryParams);
			DBObject orderBy = this.formatPrintable(orderByFormat, orbderbyParams);
			
			com = this.getCommand(dataBase, dataSet);
			if (com != null)
			{
				com.setTemp(temp);
				com.setQuery(query);
				com.setOrderBy(orderBy);
				com.setLimit(limit);
			}
		}
		return com;
	}

	@Override
	public Command findMulti(Database dataBase, DataSet dataSet, CommandTemplate temp,Object[] queryParams,Object[] orbderbyParams, long offset, long length)
	{
		MongodbCommand com = null;
		
		if (temp.getType() == CommandType.MFIND)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
			
			DBObject query = this.formatPrintable(queryFormat, queryParams);
			DBObject orderBy = this.formatPrintable(orderByFormat, orbderbyParams);
			
			com = this.getCommand(dataBase, dataSet);
			if (com != null)
			{
				com.setTemp(temp);
				com.setQuery(query);
				com.setOrderBy(orderBy);
				com.setSkip(offset);
				com.setLimit(length);
			}
		}
		return com;
	}

	@Override
	public <T> Command insertOne(Database dataBase, DataSet dataSet, T data, CommandTemplate temp,boolean upDuplicated, Map<String, Object> extParas)
	{
		MongodbCommand com = null;
		if (temp.getType() == CommandType.INSETONE)
		{
			com = this.getCommand(dataBase, dataSet);
			if (com != null)
			{
				DBObject dbObject = this.putObject(dataSet.getMyClass(), data);
				if (dbObject != null)
				{
					List<DBObject> datas = new ArrayList<DBObject>(1);
					datas.add(dbObject);
					com.setDatas(datas);
					com.setDupUpdate(upDuplicated);
					com.setTemp(temp);
				}
			}
		}
		return com;
	}
	

	@Override
	public <T> Command insertMulti(Database dataBase, DataSet dataSet, List<T> dataList,CommandTemplate temp, boolean upDuplicated, Map<String, Object> extParas)
	{
		MongodbCommand com = null;
		
		if (temp.getType() == CommandType.MINSET && dataList != null && dataList.size() > 0)
		{
			com = this.getCommand(dataBase, dataSet);
			if (com != null)
			{
				List<DBObject> datas = new ArrayList<DBObject>(dataList.size());
				for (T data : dataList)
				{
					DBObject dbObject = this.putObject(dataSet.getMyClass(), data);
					if (dbObject != null)
					{
						datas.add(dbObject);
					}
					else
					{
						this.freeCommand(dataBase, dataSet, com);
						return null;
					}
				}

				com.setDatas(datas);
				com.setDupUpdate(upDuplicated);
				com.setTemp(temp);
			}
		}
		return com;
	}

	@Override
	public Command deleteMulti(Database dataBase, DataSet dataSet, CommandTemplate temp, Object[] parameters)
	{
		MongodbCommand com = null;
		
		if (temp.getType() == CommandType.DELETE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			DBObject query = this.formatPrintable(queryFormat, parameters);
			
			if (query != null)
			{
				com = this.getCommand(dataBase, dataSet);
				if (com != null)
				{
					com.setTemp(temp);
					com.setQuery(query);
				}
			}
		}
		return com;
	}

	@Override
	public Command updateOne(Database dataBase, DataSet dataSet, CommandTemplate temp,Object[] confParas, Object[] upParas, boolean upInsert, boolean multi)
	{
		MongodbCommand com = null;
		
		if (temp.getType() == CommandType.UPDATE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			Formater updateFormat = temp.getFormat(Command.UPDATE_FORMAT);
			
			DBObject query = this.formatPrintable(queryFormat, confParas);
			DBObject update = this.formatPrintable(updateFormat, upParas);
			
			if (update != null)
			{
				com = this.getCommand(dataBase, dataSet);
				if (com != null)
				{
					com.setTemp(temp);
					com.setSet(dataSet);
					com.setQuery(query);
					com.setUpdate(update);
					com.setUpInsert(upInsert);
					com.setMulti(multi);
				}
			}
		}
		return com;
	}
	
	@Override
	public Command findAggregate(Database dataBase, DataSet dataSet,CommandTemplate temp,Object[] aggregateParas)
	{
		MongodbCommand com = null;
		
		if(temp.getType() == CommandType.AGGRET)
		{
			Formater aggregateFormat  = temp.getFormat(Command.AGGREGAT_FORMAT);
			DBObject aggregate  = this.formatPrintable(aggregateFormat, aggregateParas);
			
			if(aggregate != null)
			{
				com = this.getCommand(dataBase, dataSet);
				if(com != null)
				{
					com.setTemp(temp);
					com.setSet(dataSet);
					com.setAggregat(aggregate);
				}
			}
		}
		return com;
	}

	@Override
	public Command count(Database dataBase, DataSet dataSet, CommandTemplate temp,Object[] parameters)
	{
		MongodbCommand com = null;
		
		if (temp.getType() == CommandType.COUNT)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			DBObject query = this.formatPrintable(queryFormat, parameters);
			com = this.getCommand(dataBase, dataSet);
			if (com != null)
			{
				com.setTemp(temp);
				com.setQuery(query);
			}
		}
		return com;
	}
	
	private MongodbCommand getCommand(Database dataBase, DataSet dataSet)
	{
		MongodbCommand com = new MongodbCommand();
		com.setSet(dataSet);
		return com;
	}

	@Override
    public void freeCommand(Database dataBase, DataSet dataSet, Command command)
    {
	    
    }
	
    private <T> BasicDBObject putObject(Entity dataClass,T data)
	{		
		if(dataClass != null && data != null)
		{
			Class<?> metaClass = dataClass.getClassMeta();
			Class<?> storClass = data.getClass();			
			BasicDBObject dbObject;
			List<EntityField> fields;
			
			if(storClass.equals(metaClass))
			{
				dbObject = new BasicDBObject();
				fields = dataClass.getAllDataField();
				
				Object value = null;
				for(EntityField field:fields)
				{
					value= field.getValue(data);
					if (this.putDataField(dbObject, field, storClass, value) != 0)
					{
						return null;
					}
				}
				return dbObject;
            }
		}
		return null;
	}
    
   @SuppressWarnings("rawtypes")
   private int putDataField( BasicDBObject holder, EntityField field,Class<?> storClass,Object value)
   {      
	   if(value == null)
	   {
		   if(field.isStoreNull())
		   {
			   holder.append(field.getColumnName(), null);
		   }
		   return 0;
	   }
	  
	   switch(field.getDecType())
	   {
		   case ATOM:
		       if(holder.append(field.getColumnName(),value) != null)
		       {   
		    	   return 0;
		       }		       
		       return 1;
		   case ARRAY:
			   return this.putArray(holder,field,value);
		   case COLLECTION:
			   return this.putCollection(holder,field,(Collection)value);
		   case CLASS:
			   return this.putClass(holder,field,value);
		   case MAP:
			   return this.putMap(holder,field,(Map)value);
		   default:
			   return 1;			   
	   }
    }
   
   private int putClass( BasicDBObject holder,EntityField field,Object value )
   {
 	  Entity subDataClass = field.getSubClass();
 	  if(subDataClass != null)
 	  {
 	  	   BasicDBObject subObject = this.putObject(subDataClass, value);
    	   if(subObject == null)
    	   {
    		   return 1;
    	   }
    	   
    	   if(holder.append(field.getColumnName(), subObject) != null)
    	   {
    		   return 0;
    	   }    	   
 	  }
 	  return 1;	   
   }
	
    private int putArray( BasicDBObject holder,EntityField field,Object array )
    {  
        int size = Array.getLength(array);
        if(size == 0)
        {
 		   if(field.isStoreNull())
 		   {
 			   holder.append(field.getColumnName(), null);
 		   }
 		   return 0;
        }
        
        Entity subClass = field.getSubClass();
        if(subClass == null)
        {
        	if(holder.append(field.getColumnName(), array) != null)
        	{
        		return 0;
        	}
        	return 1;
        }
        
        Object[] entryDbObjects = new Object[size]; 
        for ( int i = 0; i < size; i++ )
        {
            entryDbObjects[i] = this.putObject(field.getSubClass(),Array.get( array, i ));
            if(entryDbObjects[i] == null)
            {
            	return 1;
            }
         }
        
        if(holder.append(field.getColumnName(), entryDbObjects) != null)
        {        
        	return 0;
        }
        else
        {
        	return 1;
        }
    }
	
    private int putCollection( BasicDBObject holder,EntityField field,Collection<?> collection )
    {
        if(collection.size() == 0)
        {
		    if(field.isStoreNull())
		    {
			    holder.append(field.getColumnName(), null);
		    }
		    return 0;
        } 
    	
        Entity subClass = field.getSubClass();
        if(subClass == null)
        {
        	if(holder.append(field.getColumnName(), collection) != null)
        	{
        		return 0;
        	}
        	return 1;
        }
    	
    	List<BasicDBObject> entryDbObjects = new  LinkedList<BasicDBObject>();
    	
        for ( Object obj: collection ) 
        {
        	BasicDBObject dbObject = this.putObject(field.getSubClass(),obj);
            if(dbObject == null)
            {
            	return 1;
            }
            entryDbObjects.add(dbObject);            
         } 
        
         if(holder.append(field.getColumnName(), entryDbObjects) != null)
         {
        	 return 0;
         }
         else
         {
        	 return 1;
         }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int putMap(BasicDBObject holder,EntityField field,Map m )
    { 
        if(m.size() == 0)
        {
		    if(field.isStoreNull())
		    {
			    holder.append(field.getColumnName(), null);
		    }
		    return 0;
        } 
    	
        Entity subClass = field.getSubClass();
        if(subClass == null)
        {
        	if(holder.append(field.getColumnName(), m) != null)
        	{
        		return 0;
        	}
        	return 1;
        }    	
        
        BasicDBObject dbObject = null;
    	Map<String,Object> entryDbObjects = new  LinkedHashMap<String,Object>();
   	
    	for ( Map.Entry entry : (Set<Map.Entry>)m.entrySet() )          
        { 
			dbObject = this.putObject(field.getSubClass(),entry.getValue());
	
            if(dbObject == null)
            {
            	return 1;
            }
            entryDbObjects.put(entry.getKey().toString(),dbObject);
        } 
        
    	if(holder.append(field.getColumnName(), entryDbObjects) != null)
        {
       	    return 0;
        }
        else
        {
       	    return 1;
        }
    }
	
    @SuppressWarnings("unchecked")
    private <T> T getObject(Entity dataClass,DBObject storObject)
	{
		if(dataClass != null && storObject != null)
		{
			Class<?> metaClass = dataClass.getClassMeta();
			List<EntityField> fields;			

			fields = dataClass.getAllDataField();
			Object data;
            try 
            {
	            data = metaClass.newInstance();
            }
            catch (InstantiationException |IllegalAccessException e1) 
            {
	            e1.printStackTrace();
	            return null;
            }
			
			for(EntityField field:fields)
			{
                if(this.getDataField(storObject, field,data) != 0)
                {
                	return null;
                } 
			}
			
			return (T)data;
		}				
		
		return null;
	}
	
   private int getDataField( DBObject holder, EntityField field,Object mother)
   {    
	   Object value = null;
	   value = holder.get(field.getColumnName());	
	   if( value == null )
	   {
		   return 0;
	   }
		   
       if(value instanceof DBObject)    	   
       {  	   
    	   switch(field.getDecType())
    	   {
    		   case ARRAY:
    			   if(this.getArray((DBObject)value, field,mother)!=0)
    			   {
    				   return 1;
    			   }
    			   break;
    		   case COLLECTION:
    			   if(this.getCollection((DBObject)value, field,mother)!=0)
    			   {
    				   return 1;
    			   }
    			   break;
    		   case MAP:
    			   if(this.getMap((DBObject)value, field,mother)!=0)
    			   {
    				   return 1;
    			   }
    			   break;
    		   case CLASS:
    			   if(this.getClass((DBObject)value, field,mother)!=0)
    			   {
    				   return 1;
    			   }
    			   break;
    		   default:    			   
    			   return 1;    			   
    	   }
    	   
    	   return 0;
       }
       else  
       { 
    	   return field.setValue(mother, value);
       }      
    }
   
   @SuppressWarnings({ "rawtypes" })
   private int getArray(DBObject holder,EntityField field,Object mother)
   { 
   	   Object eStore = null;	
   	   Object eData  = null;
       Collection sColl = (Collection)holder;
   	   Object vArray = null;
   	   int size;
  	
   	    vArray = field.getValue(mother);		
		if(  vArray != null  && !vArray.getClass().isArray())
		{
			return 1;
		}
		
		Entity subClass = field.getSubClass();
		size = sColl.size();
		if(vArray == null || Array.getLength(vArray) != size)
		{
			vArray=Array.newInstance(field.getFiedMeta().getType().getComponentType(), size);
			if(field.setValue(mother, vArray) != 0)
			{
				return 1;
			}
		}		
		
		int i=0;
	    Iterator it = sColl.iterator(); // 获得一个迭代子
        while(it.hasNext())
	    {
        	eStore = it.next(); // 得到下一个元素

	   		if(subClass == null)   //load entry data
	   		{
	   			Array.set(vArray, i, eStore);	
	   		}
	   		else  // load entry class
	   		{
	   			eData = this.getObject(subClass, (DBObject)eStore);
	   			Array.set(vArray, i, eData);
	   		}
        }
    
        return 0; 
    }
   
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int getMap(DBObject holder,EntityField field,Object mother)
    { 
    	Object eStore = null;	
    	Object eData  = null;
    	Map sMap = holder.toMap();
    	Map vMap = null;
   	
		vMap = (Map)field.getValue(mother);		
		if(vMap == null)
		{
			return 1;
		}
		
		Entity subClass = field.getSubClass();
		
	   	for ( Map.Entry entry : (Set<Map.Entry>)sMap.entrySet() )          
        {
	   		eStore = entry.getValue();
            
	   		if(subClass == null)   
	   		{
	   			vMap.put(entry.getKey().toString(), eStore);	   			
	   		}
	   		else  
	   		{
	   			eData = this.getObject(subClass, (DBObject)eStore);
	   			if(eData != null)
	   			{
	   				vMap.put(entry.getKey().toString(), eData);
	   			}	   			
	   		}
        }    

       
        return 0; 
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private int getCollection(DBObject holder,EntityField field,Object mother)
    { 
    	Object eStore = null;	
    	Object eData  = null;
    	Collection sColl = (Collection)holder;
    	Collection vColl = null;
   	
    	vColl = (Collection)field.getValue(mother);		
		if(vColl == null)
		{
			return 1;
		}
		
		Entity subClass = field.getSubClass();
		
	    Iterator it = sColl.iterator(); // 获得一个迭代子
        while(it.hasNext())
	    {
        	eStore = it.next(); // 得到下一个元素

	   		if(subClass == null 
	   				|| eStore instanceof Byte
	   				|| eStore instanceof Short
	   				|| eStore instanceof Integer
	   				|| eStore instanceof Long
	   				|| eStore instanceof Float
	   				|| eStore instanceof Double
	   				|| eStore instanceof Character
	   				|| eStore instanceof Boolean
	   				)   
	   		{
	   			Type gt = field.getFiedMeta().getGenericType();   //得到泛型类型 
	   			java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType)gt;
		        Type[] types = pt.getActualTypeArguments();
		        if(((Class<?>)types[0]).equals(Double.class))
		        {
		        	double d = Double.parseDouble(eStore.toString());
		        	vColl.add((Double.valueOf(d)));
		        }
		        else
		        {
		        	vColl.add(eStore);
		        }
	   		}
	   		else  // load entry class
	   		{
	   			eData = this.getObject(subClass, (DBObject)eStore);
	   			if(eData != null)
	   			{
	   				vColl.add(eData);
	   			}	   			
	   		}
        }
     
        return 0; 
   }

    private int getClass(DBObject holder,EntityField field,Object mother)
    { 
    	Object eData  = null;
		
		Entity subClass = field.getSubClass();
		

   		if(subClass != null)   //should not be null
   		{
  			eData = this.getObject(subClass, holder);
 
			if(field.setValue(mother, eData)!= 0)
			{
				return 1;
			}			
   		}
   		else  
   		{
   			return 1;
   		}
     
        return 0; 
   }	

	private DBObject formatPrintable(Formater format, Object[] params)
	{
		if (format != null)
		{
			String ftContent = format.getContent();
			String strObject = null;

			if (ftContent != null)
			{
				int paraNum = format.getParaNum();

				if (params != null && paraNum > 0)
				{
					if(params.length == paraNum)
					{
						try
						{
				    		strObject = String.format(ftContent, params);
							if (strObject != null)
							{
								return (DBObject) JSON.parse(strObject);
							}
						}
						catch (Exception e)
						{
							if(strObject != null)
							{
								String replaceStr = strObject.substring(strObject.indexOf(":")+1,strObject.indexOf("}")+1);
								String value = replaceStr.substring(replaceStr.indexOf(":")+1,replaceStr.indexOf("}"));
								strObject = strObject.replace(replaceStr, value);
								return (DBObject) JSON.parse(strObject);
							}
							return null;
						}
					}
					else
					{
						System.err.println("Parameter not match,Please check your parameters!");
						return null;
					}
				}
				else if (params == null || paraNum == 0) 
				{
					try
					{
						return (DBObject) JSON.parse(ftContent);
					}
					catch (Exception e)
					{
						return null;
					}
				}
				
			}
		}
		return null;
	}

	private DBObject formatUpdateAuto(DataSet dataSet, CommandTemplate temp,Map<String, Object> upKVs)
	{
		if (upKVs == null || upKVs.size() == 0)
		{
			return null;
		}

		Entity dataClass = dataSet.getMyClass();
		Class<?> metaClass = dataClass.getClassMeta();

		EntityField field = null;
		BasicDBObject holder = new BasicDBObject();
		for (Entry<String, Object> entry : upKVs.entrySet())
		{
			field = dataClass.getStorDataField(entry.getKey());
			if (field == null)
			{
				return null;
			}

			if (this.putDataField(holder, field, metaClass, entry.getValue()) != 0)
			{
				return null;
			}
		}

		BasicDBObject update = new BasicDBObject();
		if (update.append(SETOPER, holder) != null)
		{
			return update;
		}
		else
		{
			return null;
		}
	}

	@Override
	public Command updateOne(Database dataBase, DataSet dataSet, CommandTemplate temp, Object[] confParas, Map<String, Object> upKVs, boolean upInsert)
	{
		if (temp.getType() == CommandType.UPDATE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			DBObject query = this.formatPrintable(queryFormat, confParas);
			DBObject update = this.formatUpdateAuto(dataSet, temp, upKVs);

			if (update != null)
			{
				MongodbCommand com = this.getCommand(dataBase, dataSet);
				if (com != null)
				{
					com.setTemp(temp);
					com.setSet(dataSet);
					com.setQuery(query);
					com.setUpdate(update);
					com.setUpInsert(upInsert);
					return com;
				}
			}
		}

		return null;
	}
	
	/**
	 * 获取mongodb客户端连接选项
	 * 
	 * <p>connectionsPerHost=10:对mongo实例来说，每个host允许链接的最大链接数,这些链接空闲时会放入池中,如果链接被耗尽，任何请求链接的操作会被阻塞等待链接可用,推荐配置10<br/> 
	 * <p>minPoolsSize=5:当链接空闲时,空闲线程池中最大链接数<br/>>
	 * <p>threadsAllowedToBlockForConnectionMultiplier:<br/>
	 * <p>connectTimeout=10000:链接超时的毫秒数,0表示不超时,此参数只用在新建一个新链接时，推荐配置10,000<br/>
	 * <p>socketTimeout=0:此参数表示socket I/O读写超时时间,推荐为不超时，即 0 <br/>
	 * <p>socketKeepAlive=false<br/>
	 * <p>autoConnectRetry:是否尝试重连</br>
	 */
    private MongoClientOptions getClientOptions(DBServer server) 
	{
		Builder builder = new MongoClientOptions.Builder().socketKeepAlive(true) // 是否保持长链接
														   .connectionsPerHost(100) // 每台主机最大连接数
														   .maxWaitTime(2000)//最大等待超时时间
														   .threadsAllowedToBlockForConnectionMultiplier(50) // 一个socket最大的等待请求数
														   .autoConnectRetry(true);// 是否自动重试连接
		switch (server.getPolicy()) 
		{
			case DBServer.NosqlServer.READ_ONLY:
				builder.readPreference(ReadPreference.secondaryPreferred());
				break;
			case DBServer.NosqlServer.WRITE_ONLY:
				builder.readPreference(ReadPreference.primaryPreferred());
			default:
				builder.readPreference(ReadPreference.secondaryPreferred());
				break;
		}
		builder.writeConcern(server.isFsync() ? WriteConcern.FSYNC_SAFE : WriteConcern.SAFE);
		return builder.build();
	}
	
}
