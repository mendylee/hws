package com.xrk.hws.dal.db.mc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xrk.hws.dal.common.ClustClient;
import com.xrk.hws.dal.common.Command;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;
import com.xrk.hws.dal.core.CommandType;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.core.Entity;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.core.Formater;
import com.xrk.hws.dal.core.PhysicDatabase;
import com.xrk.hws.dal.core.StorIndex;

public class MemCachedClient extends ClustClient
{
	//存储在MC的数据类型常量定义
	public final static  int ID_MCDBOBJECT    = 20;
	public final static  int ID_LINKEDHASHMAP = 21;
	public final static  int ID_ARRAYLIST     = 22;
	public final static  int ID_STRINGARRAY   = 23;
	public final static  int ID_DATE          = 24;

	private static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<Kryo>(); 
	/**
	 * Memcached服务器地址
	 */
	private List<InetSocketAddress> mcServers = null;
	
	/**
	 * Memcached访问客户端对象
	 */
	public  static MemcachedClient mcClient = null;

	@Override
    public int toBeReady()
    {
		if(mcClient == null)
		{
			if(mcServers == null)
			{
				if(this.servers.isEmpty())
				{
					return 1;
				}
				mcServers = new ArrayList<InetSocketAddress>();
				Iterator<Entry<Integer, DBServer>> iter = this.servers.entrySet().iterator(); 
				while (iter.hasNext()) 
				{ 
					Entry<Integer, DBServer> entry = iter.next(); 
					DBServer server = entry.getValue();
					
					InetSocketAddress mcServer = new InetSocketAddress(server.getHost(),server.getPort());
					mcServers.add(mcServer);
				} 
				try 
				{
	                mcClient = new MemcachedClient(new BinaryConnectionFactory(),mcServers);
	                if(mcClient == null)
	                {
	                	return 1;
	                }
                }
                catch (IOException e) 
                {
	                e.printStackTrace();
	                return 1;
                }
			}			
		}

	    return 0;
    }
	
	@Override
	public int close()
	{
		if(mcClient != null)
		{
			mcClient.shutdown();
		}
		return 0;
	}

	@Override
    public Command findOne(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] parameters)
    {
		if(temp.getType() == CommandType.FINDONE)
		{
			MemcachedCommand com = this.getCommand(dataBase, dataSet);
			if(com != null)
			{
                String key = this.formatKeyByParas(dataSet.getMyClass(), temp.getFormat(Command.QUERY_FORMAT), parameters);
				if(key != null)
				{
					com.setTemp(temp);
					com.setKey(key);			
					return com;
				}
			}
		}
		
		return null;
    }

	@Override
    public <T> Command insertOne(Database dataBase, DataSet dataSet,T data, CommandTemplate temp,boolean upDuplicated, Map<String,Object> extParas)
    {
		if(temp.getType() == CommandType.INSETONE)
		{
			MemcachedCommand com = this.getCommand(dataBase, dataSet);
			if(com != null)
			{
				MemcachedDbObject dbObject = new MemcachedDbObject();
				Entity  dataClass = dataSet.getMyClass();
				if(dbObject.buildObject(dataClass, data) == 0)
				{
	                String key = this.formatKeyByData(dataClass, temp.getFormat(Command.QUERY_FORMAT), data);
					if(key != null)
					{
						Output output = new Output(new ByteArrayOutputStream()); 
						MemCachedClient.getKryo().writeObject(output, dbObject);
						com.setKey(key);
						com.setTemp(temp);
						com.setValue(output.toBytes());
						output.close();
						com.setDupUpdate(upDuplicated);	
						if(extParas != null)
						{
							Integer exp = (Integer)extParas.get(DataSet.EXPARA_EXPTIME);
							if(exp != null)
							{
								com.setExp(exp);
							}
						}
						
						return com;
					}
				}				
			}
		}
		
		return null;
    }

	@Override
    public <T> Command insertMulti(Database dataBase, DataSet dataSet,List<T> data, CommandTemplate temp,boolean upDuplicated, Map<String,Object> extParas)
    {
	    return null;
    }
	
	@Override
    public Command deleteMulti(Database dataBase, DataSet dataSet,CommandTemplate temp, Object[] parameters)
    {
		if(temp.getType() == CommandType.DELETE)
		{
			/*get query  and update format,fields format not supported now*/
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			String key = this.formatKeyByParas(dataSet.getMyClass(), queryFormat,parameters);
			
			/*query should not be void*/
			if(key != null)
			{
				MemcachedCommand com = this.getCommand(dataBase, dataSet);
				if(com != null)
				{
					com.setTemp(temp);
					com.setKey(key);
					return com;
				}
			}
		}
		
	    return null;
    }

	@Override
    public void freeCommand(Database dataBase, DataSet dataSet, Command command)
    {
	    
    }

	@Override
    public <T> T findOne(PhysicDatabase db, DataSet set, Command command)
    {
		byte[] storeObject = (byte[])mcClient.get(((MemcachedCommand)command).getKey());
		Input input = null;
		
		if(storeObject == null)
		{
			return null;
		}
		try
		{
			input = new Input(storeObject);
			MemcachedDbObject mcObject = (MemcachedDbObject)MemCachedClient.getKryo().readObject(input,MemcachedDbObject.class);
			if(mcObject != null)
			{
				return mcObject.takeObject(set.getMyClass());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
    }

	@Override
    public int insertOne(PhysicDatabase db, DataSet set, Command command)
    {
		String key = ((MemcachedCommand)command).getKey();
		Object value = ((MemcachedCommand)command).getValue();
		int exp       = ((MemcachedCommand)command).getExp();
		try
		{
			if(((MemcachedCommand)command).isDupUpdate())
			{
				mcClient.set(key, exp, value);
			}
			else
			{
				mcClient.add(key, exp, value);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 1;
		}
		
	    return 0;
    }

	@Override
    public int insertMulti(PhysicDatabase db, DataSet set, Command command)
    {
	    return -1;
    }
	
	@Override
    public int deleteMulti(PhysicDatabase db, DataSet set, Command command)
    {
		try
		{
			mcClient.delete(((MemcachedCommand)command).getKey());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	        
		return 0;
	 }
	

	private MemcachedCommand getCommand(Database dataBase, DataSet dataSet)
	{
		MemcachedCommand com = new MemcachedCommand();
		com.setSet(dataSet);
		return com;

	}

	public static Kryo getKryo()
    {
		Kryo kryo = threadLocalKryo.get();
	    if(kryo == null)
	    {
	       return MemCachedClient.buildKryo();
	    }
	    
	    return kryo;
    }

	public static Kryo buildKryo()
	{
		Kryo kryo = new Kryo();
		kryo.register(MemcachedDbObject.class, ID_MCDBOBJECT);
		kryo.register(LinkedHashMap.class, ID_LINKEDHASHMAP);
		kryo.register(ArrayList.class, ID_ARRAYLIST);
		kryo.register(String[].class, ID_STRINGARRAY);
		kryo.register(Date.class, ID_DATE);
		threadLocalKryo.set(kryo);
		return kryo;
	}
	
	private String formatKeyByData(Entity dataClass, Formater format, Object data)
	{
		StorIndex pk = dataClass.getIndex();
		if (pk != null)
		{
			Object[] params = pk.getIndexValues(data);
			return this.formatKeyByParas(dataClass, format, params);
		}

		return null;
	}

	private String formatKeyByParas(Entity dataClass, Formater format, Object[] paras)
	{
		if (format != null && paras != null)
		{
			String ftContent = format.getContent();

			if (ftContent != null)
			{
				int paraNum = format.getParaNum();

				if (paraNum > 0 && (paras.length == paraNum))
				{
					try
					{
						String strObject = String.format(ftContent, paras);
						if (strObject != null)
						{
							return strObject;
						}
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
}
