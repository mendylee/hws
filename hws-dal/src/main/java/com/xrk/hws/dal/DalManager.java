package com.xrk.hws.dal;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.common.ClustClient;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;
import com.xrk.hws.dal.core.CommandType;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.core.DBServer.MysqlServer;
import com.xrk.hws.dal.core.DBServer.NosqlServer;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.core.Entity;
import com.xrk.hws.dal.core.Formater;
import com.xrk.hws.dal.core.PhysicDatabase;

/**  
 * 类: DAL数据访问层管理.
 * 
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月6日
 * <br> JDK版本：1.7
 * <br>==========================     
 */
public class DalManager
{
	/**
	 * 
	 * 类: 配置文件管理. <br/>  
	 *
	 * <br>==========================
	 * <br> 公司：广州向日葵信息科技有限公司
	 * <br> 开发：lijp
	 * <br> 版本：1.0
	 * <br> 创建时间：2014年5月6日
	 * <br> JDK版本：1.7
	 * <br>==========================
	 */
	private static class DalManagerConfig
	{
		public static  String ROOT         = "configs";				
		public static  String CLUSTCLIENTS = "clustclientconfs";	
		public static  String CLUSTCLIENT  = "clustclientconf";		//客户端
		public static  String DATABASES    = "databaseconfs";
		public static  String DATABASE     = "databaseconf";
		public static  String DATASETS     = "datasetconfs";
		public static  String DATASET      = "datasetconf";
	}
	
	/**
	 * 
	 * 类: 集群配置. <br/>  
	 *
	 * <br>==========================
	 * <br> 公司：广州向日葵信息科技有限公司
	 * <br> 开发：lijp
	 * <br> 版本：1.0
	 * <br> 创建时间：2014年5月6日
	 * <br> JDK版本：1.7
	 * <br>==========================
	 */
	private static class ClusterConfig
	{
		public static  String ROOT           = "clustclients";
		public static  String CLIENT         = "clustclient";
		public static  String CLIENTNAME     = "name";
		public static  String TYPENAME 	 	 = "type";
		public static  String CLIENTCLASS    = "class";
		public static  String CLIENTSERVERS  = "servers";
		public static  String CLIENTSERVER   = "server";
		public static  String SERVERNAME     = "name";
		public static  String SERVERID       = "id";
		public static  String SERVERPOLICY	 = "policy";
		public static  String SERVERFSYNC    = "fsync";
		public static  String SERVERHOST     = "host";
		public static  String SERVERPORT     = "port";
		public static  String SERVERURL		 = "url";
		public static  String SERVERDRIVER	 = "driver";
		public static  String SERVERUSER	 = "user";
		public static  String SERVERSCHEMA	 = "schema";
		public static  String SERVERPASS	 = "pass";
	}
	
	/**
	 * 
	 * 类: 数据库配置. <br/>  
	 *
	 * <br>==========================
	 * <br> 公司：广州向日葵信息科技有限公司
	 * <br> 开发：lijp
	 * <br> 版本：1.0
	 * <br> 创建时间：2014年5月6日
	 * <br> JDK版本：1.7
	 * <br>==========================
	 */
	private static class DatabaseConfig
	{
		public static  String ROOT             = "databases";
		public static  String DATABASE         = "database";
		public static  String DATABASENAME     = "name";
		public static  String DATABASISVIRTUAL = "isvirtual";
		public static  String DATABASECLASS    = "class";
		public static  String DATABASECLIENT   = "client";
	}
	
	/**
	 * 
	 * 类: 数据结果集配置. <br/>  
	 *
	 * <br>==========================
	 * <br> 公司：广州向日葵信息科技有限公司
	 * <br> 开发：lijp
	 * <br> 版本：1.0
	 * <br> 创建时间：2014年5月6日
	 * <br> JDK版本：1.7
	 * <br>==========================
	 */
	private static class DataSetConfig
	{
		public static  String ROOT             = "packages";
		public static  String PACKAGE          = "package";
		public static  String PACKAGENAME      = "name";
		public static  String DATASET          = "dataset";
		public static  String DSDB             = "db";
		public static  String DSDATACLASS      = "dataclass";
		public static  String DSNAME           = "name";
		public static  String DSISCACHE        = "iscache";
		public static  String DSCLASS          = "class";
		public static  String COMTEMPLATES     = "comtemplates";
		public static  String COMTEMPLATE      = "comtemplate";
		public static  String COMTEMPLATENAME  = "name";
		public static  String COMTEMPLATETYPE  = "type";
		public static  String FORMAT           = "format";
		public static  String FORMATNAME       = "name";
		public static  String FORMATPARANUM    = "paranum";
	}
	
	/**
	 * 服务器集群客户端容器
	 */
	private Map<String,ClustClient> clients 	= new LinkedHashMap<String,ClustClient>();
	/**
	 * 数据库集群容器
	 */
	private Map<String,Database> 	databases   = new LinkedHashMap<String,Database>();
	/**
	 * 数据结果集容器
	 */
	private Map<String,DataSet> 	dataSets 	= new LinkedHashMap<String,DataSet>();
	/**
	 * 数据结果集对象容器
	 */
	private Map<String,Entity> 	dataClasses = new LinkedHashMap<String,Entity>();
	/**
	 * 数据访问层管理单例
	 */
	private static DalManager manager = null;
	
	/**
	 * 
	 * 获取单例  
	 *    
	 * @return
	 */
	public synchronized static DalManager getInstance()
	{
		if(null == manager)
		{
			manager = new DalManager();
		}
		
		return manager;
	}
	
	/**
	 * 
	 * 添加集群服务器  
	 *    
	 * @param client	服务器客户端
	 * @return			成功返回0，反之1
	 */
	public int addClient(ClustClient client)
	{
		if(clients.get(client.getName()) == null)
		{
			clients.put(client.getName(), client);
			return 0;
		}
		return 1;
	}
	
	/**
	 * 
	 * 根据指定的集群客户端名称，获取集群客户端 
	 *    
	 * @param clientName	集群客户端名称
	 * @return
	 */
	public ClustClient getClient(String clientName)
	{
		return clients.get(clientName);
	}

	/**
	 * 
	 * 添加集群数据库  
	 *    
	 * @param database	数据库对象
	 * @return			成功返回0，失败返回1
	 */
	public int addDatabase(Database database)
	{
		if(databases.get(database.getName()) == null)
		{
			databases.put(database.getName(), database);
			return 0;
		}
		return 1;
	}
	
	/**
	 * 
	 * 根据数据库名称获取元数据 对象  
	 *    
	 * @param dbName	数据库名称
	 * @return			元数据对象
	 */
	public Database getDatabase(String dbName)
	{
		return databases.get(dbName);
	}
	
	/**
	 * 
	 * 添加存储数据结果集  
	 *    
	 * @param key	数据结果集key
	 * @param set	数据结果集对象
	 * @return		成功返回0，失败返回1
	 */
	public int addDataSet(String key,DataSet set)
	{
		
		if (dataSets.get(key) == null)
		{
			dataSets.put(key,set);
			return 0;
		}	

		return 1;
	}
	
	/**
	 * 
	 * 根据类名称获取结果集对象  
	 *    
	 * @param className	类名称
	 * @return			结果集对象
	 */
	public <T> DataSet getDataSet(Class<T> clazz)
	{
		assert clazz != null;
		
		DataSet dataSet = dataSets.get(clazz.getName());
		if(dataSet == null)
		{
			Logger.error("Get dataSet fail. className: %s", clazz);
			return null;
		}
		return dataSet;
	}
	
	/**
	 * 
	 * 添加数据类对象到缓存中  
	 *    
	 * @param dataClass	数据类对象
	 * @return			成功返回0，反之：1
	 */
	public int addDataClass(Entity dataClass)
	{
		String name = dataClass.getFullName();
		if(dataClasses.get(name) == null)
		{
			dataClasses.put(name,dataClass);
			return 0;
		}
		return 1;
	}
	
	/**
	 * 
	 * 根据类文件完整名称获取数据类对象  
	 *    
	 * @param clsFullName	类文件完整路径名称
	 * @return				数据类文件对象
	 */
	public Entity getDataClass(String clsFullName)
	{
		return dataClasses.get(clsFullName);
	}
	
	/**
	 * 
	 * 移除数据类对象  
	 *    
	 * @param dataClass		需要移除的数据类对象
	 * @return
	 */
	public int removeDataClass(Entity dataClass)
	{
		if(dataClass != null)
		{
			String dataClassName = dataClass.getFullName();
			if(dataClassName != null)
			{
				dataClasses.remove(dataClassName);
				return 0;
			}
		}
		return 1;
	}
	
	public static int init(String configFile)
	{
		return init(configFile, false, null, null);
	}
	
	public static int init(String configFile, boolean isAbsolutePath)
	{
		return init(configFile, isAbsolutePath, null, null);
	}
	
	public static int init(String configFile, Class<?> classLoader)
	{
		return init(configFile, false, null, classLoader);
	}
	
	public static int init(String configFile, boolean isAbsolutePath, Class<?> classLoader)
	{
		return init(configFile, isAbsolutePath, null, classLoader);
	}
	
	/**
	 * 
	 * 初始化数据访问存储服务，从配置文件中加载所有实例化的数据库和结果集对象 
	 * @param configFile	配置文件
	 *
	 */
	public static int init(String configFile, boolean isAbsolutePath, String basePath, Class<?> classLoader)
	{
		String curDir = null;
		
		Properties property = System.getProperties();
		curDir = property.getProperty("user.dir");
		if(null == configFile)
		{
			return 1;
		}
		
		String fileDir = "";
		if(isAbsolutePath)
		{
			if (basePath == null || basePath.isEmpty())
			{
				File file = new File(configFile);
				fileDir = file.getParent();
			}
			else
			{
				fileDir = basePath;
			}
		}
		
		InputStream in = locateFromClasspath(configFile, isAbsolutePath, classLoader);		
		property.setProperty("user.dir", configFile);
		
		try
		{
			//读取主配置文件加载所有子配置文件
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db;
	        db = factory.newDocumentBuilder();
	        org.w3c.dom.Document doc;
	        doc = db.parse(in);
	        doc.normalize();
	        
	        //获取XML根节点列表
	        NodeList rootNodes = doc.getElementsByTagName(DalManagerConfig.ROOT);
	    	
	        if(rootNodes.getLength() != 1)
	        {
	        	property.setProperty("user.dir",curDir);
	        	return 1;
	        }
	        
	        //获取根节点的子节点列表
	        NodeList confNodes =  rootNodes.item(0).getChildNodes();	        
	        
    		List<String> clientConfs = new ArrayList<String>();
    		List<String> databaseConfs = new ArrayList<String>();
    		List<String> dataSetConfs = new ArrayList<String>();
    		
    		Node confNode;
    		String confNodeName;
    		NodeList childNodes;
    		Node confChildNode;
    		
	        for(int n=0;n<confNodes.getLength();n++)
	        {
	        	confNode = confNodes.item(n);
	        	if(confNode.getNodeType() != Node.ELEMENT_NODE)
	        	{
	        		continue;
	        	}
	        	confNodeName = confNode.getNodeName();
        		childNodes = confNode.getChildNodes();
        		
	        	if (confNodeName.equals(DalManagerConfig.CLUSTCLIENTS))
	        	{
	        		for(int i=0;i<childNodes.getLength();i++)
	        		{	        			
	        			confChildNode =  childNodes.item(i);
	        			if(confChildNode.getNodeType() != Node.ELEMENT_NODE)
	    	        	{
	    	        		continue;
	    	        	}
	        			if(confChildNode.getNodeName().equals(DalManagerConfig.CLUSTCLIENT))
	        			{
	        				clientConfs.add(confChildNode.getTextContent());
	        			}
	        			else
	        			{
	        				System.out.printf("configure file has no supportted node %s \r\n",confChildNode.getNodeName());
	        				property.setProperty("user.dir",curDir);
	        				return 1;
	        			}        			
	        		}
	        	}
	        	else if(confNodeName.equals(DalManagerConfig.DATABASES))
	        	{
	        		for(int i=0;i<childNodes.getLength();i++)
	        		{
	        			confChildNode =  childNodes.item(i);
	        			if(confChildNode.getNodeType() != Node.ELEMENT_NODE)
	    	        	{
	    	        		continue;
	    	        	}
	        			if(confChildNode.getNodeName().equals(DalManagerConfig.DATABASE))
	        			{
	        				databaseConfs.add(confChildNode.getTextContent());	 
	        			}	        			
	        			else
	        			{
	        				System.out.printf("configure file has no supportted node %s \r\n",confChildNode.getNodeName());
	        				
	        				property.setProperty("user.dir",curDir);
	        				return 1;
	        			}        	
	        			       			
	        		}
	        	}
	        	else if(confNodeName.equals(DalManagerConfig.DATASETS))
	        	{
	        		for(int i=0;i<childNodes.getLength();i++)
	        		{
	        			confChildNode =  childNodes.item(i);
	        			if(confChildNode.getNodeType() != Node.ELEMENT_NODE)
	    	        	{
	    	        		continue;
	    	        	}
	        			if(confChildNode.getNodeName().equals(DalManagerConfig.DATASET))
	        			{
	        				dataSetConfs.add(confChildNode.getTextContent());
	        			}	        			
	        			else
	        			{
	        				System.out.printf("configure file has no supportted node %s \r\n",confChildNode.getNodeName());
	        				property.setProperty("user.dir",curDir);
	        				return 1;
	        			}        	
	        				        			
	        		}
	        	}
	        	else
	        	{
	        		System.out.printf("configure file has no supportted node %s \r\n",confNodeName);
	        		property.setProperty("user.dir",curDir);
	        		return 1;
	        	}
	        }
	        
	        //创建数据存储管理实例
	        manager = DalManager.getInstance();
			//加载客户集群
	        for(String conf:clientConfs)
	        {
	        	if(isAbsolutePath)
	        	{
	        		conf = String.format("%s/%s", fileDir, conf);
	        	}
	        	if(manager.loadClients(conf, isAbsolutePath, classLoader) != 0)
	        	{
	        		property.setProperty("user.dir",curDir);
	        		return 1;
	        	}
	        }
	        //加载每一个配置文件下的database
	        for(String conf:databaseConfs)
	        {
	        	if(isAbsolutePath)
	        	{
	        		conf = String.format("%s/%s", fileDir, conf);
	        	}
	        	
	        	if(manager.loadDataBases(conf, isAbsolutePath, classLoader) != 0)
	        	{
	        		property.setProperty("user.dir",curDir);
	        		return 1;
	        	}
	        }
	        
	        //加载所有的数据集配置文件
	        for(String conf:dataSetConfs)
	        {
	        	if(isAbsolutePath)
	        	{
	        		conf = String.format("%s/%s", fileDir, conf);
	        	}
	        	if(manager.loadDataSets(conf, isAbsolutePath, classLoader) != 0)
	        	{
	        		property.setProperty("user.dir",curDir);
	        		return 1;
	        	}
	        }  
		}
		catch(ParserConfigurationException |SAXException | IOException e)
		{
			Logger.error(e, "DalManager load instance from configs Error,[Error msg]:%s", e);
			property.setProperty("user.dir", curDir);
			return 1;
		}
		
		return manager.toBeReady();
	}

	/**  
	 * 加载客户端集群配置文件  
	 *    
	 * @param configFilePath		客户端集群配置文件路径
	 * @return    
	 */
    private int loadClients(String configFilePath, boolean isAbsolutePath, Class<?> classLoader)
    {
    	assert !StringUtils.isBlank(configFilePath);
        try 
        {
        	//读取主配置文件并加载所有的子配置文件 
        	InputStream in = locateFromClasspath(configFilePath, isAbsolutePath, classLoader);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
	        db = factory.newDocumentBuilder();
	        org.w3c.dom.Document doc;
	        doc = db.parse(in);
	        doc.normalize();
	        
	        NodeList rootNodes = doc.getElementsByTagName(ClusterConfig.ROOT);
	
	        if(rootNodes.getLength() != 1)
	        {
	        	return 1;
	        }
	        
	        NodeList confNodes =  rootNodes.item(0).getChildNodes();	        
	        
     		Node confNode;
    		String confNodeName;
    		NodeList childNodes;
    		Node confChildNode;
    		NodeList subNodes;
    		Node confsubNode;
    		
	        for(int n=0;n<confNodes.getLength();n++)
	        {
	        	confNode = confNodes.item(n);
	        	if(confNode.getNodeType() != Node.ELEMENT_NODE)
	        	{
	        		continue;
	        	}
	        	
	        	confNodeName = confNode.getNodeName();
        		childNodes = confNode.getChildNodes();

	        	if (confNodeName.equals(ClusterConfig.CLIENT))   		
	        	{
		        	NamedNodeMap clientAttrMap = confNode.getAttributes();
		        	Node clientNameNode;
		        	Node TypeNameNode;
		        	Node clientClassNode;
		        	String clientName;
		        	String typeName;
		        	String clientClass;
		        	ClustClient client;
		        	if((clientAttrMap != null)
		        		&& ((clientNameNode = clientAttrMap.getNamedItem(ClusterConfig.CLIENTNAME)) != null)
		        		&& ((TypeNameNode = clientAttrMap.getNamedItem(ClusterConfig.TYPENAME)) != null)
		        		&& ((clientClassNode = clientAttrMap.getNamedItem(ClusterConfig.CLIENTCLASS)) != null)
		        		&& ((clientName = clientNameNode.getNodeValue()) != null)
		        		&& ((typeName = TypeNameNode.getNodeValue()) != null)
		        		&& ((clientClass = clientClassNode.getNodeValue()) != null)
		        	  )
		        	{
			        	try 
			        	{

                            client = (ClustClient)Class.forName(DalManager.class.getPackage().getName()+"."+ clientClass).newInstance();
                            client.setName(clientName);	 
                            client.setType(typeName);
                            this.addClient(client);
	                    }
	                    catch (ClassNotFoundException |InstantiationException | IllegalAccessException e) 
	                    {
		                    System.out.printf("Fail to load class %s\r\n",DalManager.class.getPackage().getName()+"."+ clientClass);
		                    Logger.error("Fail to load class %s\r\n", DalManager.class.getPackage().getName()+"."+ clientClass);
		                    return 1;
	                    }
		        	}
		        	else
		        	{
		        		System.out.printf("Attribute lost in node %s \r\n",confNodeName);
		        		Logger.warn("Attribute lost in node %s \r\n", confNodeName);
		        		return 1;
		        	}
		        	
		        	//解析clusterclient结点下的集群服务
	        		for(int i=0;i<childNodes.getLength();i++)
	        		{	        			
	        			confChildNode =  childNodes.item(i);
	        			if(confChildNode.getNodeType() != Node.ELEMENT_NODE)
	    	        	{
	    	        		continue;
	    	        	}
	        			if(confChildNode.getNodeName().equals(ClusterConfig.CLIENTSERVERS))
	        			{       				
	        				subNodes = confChildNode.getChildNodes();	
	        				for(int j=0;j<subNodes.getLength();j++)
	        				{
	        					confsubNode = subNodes.item(j);
			        			if(confsubNode.getNodeType() != Node.ELEMENT_NODE)
			    	        	{
			    	        		continue;
			    	        	}
			        			if(confsubNode.getNodeName().equals(ClusterConfig.CLIENTSERVER))
			        			{
			    		        	NamedNodeMap serverAttrMap = confsubNode.getAttributes();
			    		        	Node serverNameNode;
			    		        	Node serverIdNode;
			    		        	Node serverPolicyNode;
			    		        	Node serverFsyncNode;
			    		        	Node serverHostNode;
			    		        	Node serverPortNode;
			    		        	
			    		        	Node serverUrlNode;
			    		        	Node serverDriverNode;
			    		        	Node serverUserNode;
			    		        	Node serverSchemaNode;
			    		        	Node serverPassNode;
			    		        	
			    		        	String serverName;
			    		        	String serverId;
			    		        	String serverPolicy;
			    		        	String serverFsync;
			    		        	String serverHost;
			    		        	String serverPort;
			    		        	
			    		        	String serverUrl;
			    		        	String serverDriver;
			    		        	String serverUser;
			    		        	String serverSchema;
			    		        	String serverPass;
			    		        	
			    		        	if((serverAttrMap != null) && typeName.equals("1"))
			    		        	{
				    		        	if(((serverNameNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERNAME)) != null)
						    			           && ((serverIdNode   = serverAttrMap.getNamedItem(ClusterConfig.SERVERID))   != null)
						    			           && ((serverUrlNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERURL)) != null)
						    			           && ((serverDriverNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERDRIVER)) != null)
						    			           && ((serverUserNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERUSER)) != null)
						    			           && ((serverPassNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERPASS)) != null)
						    			           && ((serverId   = serverIdNode.getNodeValue())   != null)
						    			           && ((serverUrl = serverUrlNode.getNodeValue()) != null)
						    			           && ((serverDriver = serverDriverNode.getNodeValue()) != null)
						    			           && ((serverUser = serverUserNode.getNodeValue()) != null)
						    			           && ((serverPass = serverPassNode.getNodeValue()) != null)
						    			           )
						    			{
				    		        		MysqlServer builder = new DBServer.MysqlServer(serverUrl,serverDriver,serverUser,serverPass);
				    		        		DBServer server = builder.sid(Integer.parseInt(serverId)).build();
				    		        		client.addServer(server);
						    			}
				    			        else
				    			        {
			    			        		System.out.printf("Attribute lost in node %s \r\n",confNodeName);
			    			        		Logger.warn("Attribute lost in node %s", confNodeName);
			    			        		return 1;
				    			        } 
			    		        	}
			    		        	else
			    		        	{
				    		        	if(((serverNameNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERNAME)) != null)
						    			           && ((serverHostNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERHOST)) != null)
						    			           && ((serverPortNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERPORT)) != null)
						    			           && ((serverName = serverNameNode.getNodeValue()) != null)
						    			           && ((serverHost = serverHostNode.getNodeValue()) != null)
						    			           && ((serverPort = serverPortNode.getNodeValue()) != null)
						    			           )
						    			{
				    		        		DBServer server = null;
				    		        		NosqlServer builder = new DBServer.NosqlServer(serverHost,Integer.parseInt(serverPort),serverName);
				    		        		if(((serverIdNode   = serverAttrMap.getNamedItem(ClusterConfig.SERVERID))   != null)
					    		        			&& ((serverPolicyNode  = serverAttrMap.getNamedItem(ClusterConfig.SERVERPOLICY))  != null)
					    		        			&& ((serverFsyncNode  = serverAttrMap.getNamedItem(ClusterConfig.SERVERFSYNC))  != null)
					    		        			&& ((serverUserNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERUSER)) != null)
								    			    && ((serverSchemaNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERSCHEMA)) != null)
								    			    && ((serverPassNode = serverAttrMap.getNamedItem(ClusterConfig.SERVERPASS)) != null)
								    			    && ((serverId   = serverIdNode.getNodeValue())   != null)
						    			            && ((serverPolicy = serverPolicyNode.getNodeValue())!= null)
						    			            && ((serverFsync = serverFsyncNode.getNodeValue())!= null) 
					    		        			&& ((serverUser = serverUserNode.getNodeValue()) != null) 
					    		        			&& ((serverSchema = serverSchemaNode.getNodeValue()) != null)
					    		        			&& ((serverPass = serverPassNode.getNodeValue()) != null))
				    		        		{
				    		        			server = builder.sid(Integer.parseInt(serverId))
				    		        							.policy(Integer.parseInt(serverPolicy))
				    		        							.fsync(Boolean.parseBoolean(serverFsync))
				    		        					        .user(serverUser)
				    		        					        .schema(serverSchema)
				    		        					        .pass(serverPass)
				    		        					        .build();
				    		        		}
				    		        		if(((serverIdNode   = serverAttrMap.getNamedItem(ClusterConfig.SERVERID))   != null)
					    		        			&& ((serverPolicyNode  = serverAttrMap.getNamedItem(ClusterConfig.SERVERPOLICY))  != null)
					    		        			&& ((serverFsyncNode  = serverAttrMap.getNamedItem(ClusterConfig.SERVERFSYNC))  != null)
					    		        			&& ((serverId   = serverIdNode.getNodeValue())   != null)
						    			            && ((serverPolicy = serverPolicyNode.getNodeValue())!= null)
						    			            && ((serverFsync = serverFsyncNode.getNodeValue())!= null))
				    		        		{
				    		        			server = builder.sid(Integer.parseInt(serverId)).policy(Integer.parseInt(serverPolicy)).build();
				    		        		}
				    		        		else
				    		        			server = builder.build();
				    		        		
				    		        		client.addServer(server);
						    			}
				    			        else
				    			        {
			    			        		System.out.printf("Attribute lost in node %s \r\n",confNodeName);
			    			        		Logger.warn("Attribute lost in node %s", confNodeName);
			    			        		return 1;
				    			        } 
			    		        	}
			        			}
			        			else
			        			{
			        				System.out.printf("configure file has no supportted node %s \r\n",confsubNode.getNodeName());
			        				Logger.warn("configure file has no supportted node %s", confsubNode.getNodeName());
			        				return 1;			        				
			        			}
		        			}
	        			}
	        			else
	        			{
	        				System.out.printf("configure file has no supportted node %s \r\n",confChildNode.getNodeName());
	        				Logger.warn("configure file has no supportted node %s", confChildNode.getNodeName());
	        				return 1;
	        			}        			
	        		}
	        	}
	        	else
	        	{
	        		System.out.printf("configure file has no supportted node %s \r\n",confNodeName);
	        		Logger.warn("configure file has no supportted node %s", confNodeName);
	        		return 1;
	        	}
	        	
	        }  
			return 0;
        }
        catch (ParserConfigurationException |SAXException | IOException e) 
        {
	        e.printStackTrace();
	        Logger.error("Fail to load clients %s", e);
	        return 1;
        }
    }
    
	/**  
	 * 加载数据库 
	 *    
	 * @param configFilePath		数据库配置文件路径
	 * @return    
	 */
    private int loadDataBases(String configFilePath, boolean isAbsolutePath, Class<?> classLoader)
    {
    	assert !StringUtils.isBlank(configFilePath);
    	
    	 try 
         {
			InputStream in = locateFromClasspath(configFilePath, isAbsolutePath, classLoader);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			org.w3c.dom.Document doc;
			doc = db.parse(in);
			doc.normalize();
 	        
 	        NodeList rootNodes = doc.getElementsByTagName(DatabaseConfig.ROOT);
 	
 	        if(rootNodes.getLength() != 1)
 	        {
 	        	return 1;
 	        }
 	        
 	        NodeList confNodes =  rootNodes.item(0).getChildNodes();	        
 	        
      		Node confNode;
     		String confNodeName;
     		
 	        for(int n=0;n<confNodes.getLength();n++)
 	        {
 	        	confNode = confNodes.item(n);
 	        	if(confNode.getNodeType() != Node.ELEMENT_NODE)
 	        	{
 	        		continue;
 	        	}
 	        	
 	        	confNodeName = confNode.getNodeName();
  
 	        	if (confNodeName.equals(DatabaseConfig.DATABASE))   		
 	        	{
 		        	NamedNodeMap dbAttrMap = confNode.getAttributes();
 		        	Node dbNameNode;
 		        	Node dbClassNode;
 		        	Node clientNode;
 		        	Node isVirtualNode;
 		        	String dbName;
 		        	String dbClass;
 		        	String clientName;
 		        	String isVirtual;
 		        	Database database;
 		        	if(    (dbAttrMap != null)
 		        		&& ((dbNameNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASENAME)) != null)
 		        		&& ((dbClassNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASECLASS)) != null)
 		        		&& ((clientNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASECLIENT)) != null)
 		        		&& ((isVirtualNode = dbAttrMap.getNamedItem(DatabaseConfig.DATABASISVIRTUAL)) != null)
 		        		&& ((dbName = dbNameNode.getNodeValue()) != null)
 		        		&& ((dbClass = dbClassNode.getNodeValue()) != null)
 		        		&& ((clientName = clientNode.getNodeValue()) != null)
 		        		&& ((isVirtual = isVirtualNode.getNodeValue()) != null)
 		        	  )
 		        	{		        		
 			        	try 
 			        	{

 			        		database = (Database)Class.forName(DalManager.class.getPackage().getName()+"."+ dbClass).newInstance();
 			        		database.setName(dbName);	 
                            this.addDatabase(database);
 	                    }
 	                    catch (ClassNotFoundException |InstantiationException | IllegalAccessException e) 
 	                    {
 		                    e.printStackTrace();
 		                    return 1;
 	                    }
 			        	
 			        	if(isVirtual.equals("0") || isVirtual.equals("false"))
 			        	{
 			        		ClustClient client = this.getClient(clientName);
 			        		if(client == null || !(database instanceof PhysicDatabase))
 			        		{
 			        			System.out.printf("Error in DB(%s) configure,failed to find client %s \r\n",dbName,clientName);
 			        			Logger.warn("Error in DB(%s) configure,failed to find client %s", dbName,clientName);
 			        			return 1;
 			        		}
 			        		
 			        		((PhysicDatabase)database).setClient(client);
 			        	}
 		        	}
 		        	else
 		        	{
 		        		System.out.printf("Attribute lost in node %s \r\n",confNodeName);
 		        		Logger.warn("Attribute lost in node %s", confNodeName);
 		        		return 1;
 		        	}
 	        	}
 	        	else
 	        	{
 	        		System.out.printf("configure file has no supportted node %s \r\n",confNodeName);
 	        		Logger.warn("Configure file has no supportted node", confNodeName);
 	        		return 1;
 	        	}
 	        	
 	        }  
 	        
 			return 0;
 	     
         }
         catch (ParserConfigurationException |SAXException | IOException e) 
         {
 	        e.printStackTrace();
 	        Logger.error("Fail to load database %s", e);
 	        return 1;
         }
    }
	
	/**  
	 * 加载数据集  
	 *    
	 * @param configFilePath		数据集配置文件路径
	 * @return    
	 */
    private int loadDataSets(String configFilePath, boolean isAbsolutePath, Class<?> classLoader)
    {
    	assert !StringUtils.isBlank(configFilePath);
    	
    	try
        {
			InputStream in = locateFromClasspath(configFilePath, isAbsolutePath, classLoader);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = factory.newDocumentBuilder();
			org.w3c.dom.Document doc;
			doc = db.parse(in);
			doc.normalize();
			
			NodeList rootNodes = doc.getElementsByTagName(DataSetConfig.ROOT);
			
	        if(rootNodes.getLength() != 1)
	        {
	        	return 1;
	        }
	        
	        NodeList confNodes =  rootNodes.item(0).getChildNodes();	        
	        
     		Node confNode;
    		String confNodeName;
    		NodeList childNodes;
    		Node confChildNode;
    		NodeList subNodes;
    		Node confsubNode;
    		NodeList sub2Nodes;
    		Node confsub2Node;
       		NodeList sub3Nodes;
    		Node confsub3Node;
    		
	        for(int n=0;n<confNodes.getLength();n++)
	        {
	        	confNode = confNodes.item(n);
	        	if(confNode.getNodeType() != Node.ELEMENT_NODE)
	        	{
	        		continue;
	        	}
	        	
	        	confNodeName = confNode.getNodeName();
        		childNodes = confNode.getChildNodes();

	        	if (confNodeName.equals(DataSetConfig.PACKAGE))   		
	        	{
		        	NamedNodeMap packageAttrMap = confNode.getAttributes();
		        	Node packageNameNode;
		        	String packageName;
					if ((packageAttrMap != null)
					        && ((packageNameNode = packageAttrMap.getNamedItem(DataSetConfig.PACKAGENAME)) != null)
					        && ((packageName = packageNameNode.getNodeValue()) != null))
					{
					}
		        	else
		        	{
		        		System.out.printf("Attribute lost in node %s \r\n",confNodeName);
		        		return 1;
		        	}
		        	
	        		for(int i=0;i<childNodes.getLength();i++)
	        		{	        			
	        			confChildNode =  childNodes.item(i);
	        			if(confChildNode.getNodeType() != Node.ELEMENT_NODE)
	    	        	{
	    	        		continue;
	    	        	}
	        			if(confChildNode.getNodeName().equals(DataSetConfig.DATASET))
	        			{ 
	        				
	    		        	NamedNodeMap datasetAttrMap = confChildNode.getAttributes();
	    		        	Node dsDbNode;
	    		        	Node dsDataClassNode;
	    		        	Node dsClassNode;
	    		        	Node dsNameNode;
	    		        	Node dsIsCacheNode;
	    		        	String dsName;
	    		        	String dsIsCache;
	    		        	String dsDb;
	    		        	String dsDataClass;
	    		        	String dsClass;
	    		        	DataSet dataSet;
	    		        	Database dataBase;
	    		        	Entity dataClass;

	    		        	if(   (datasetAttrMap != null)
	    			           && ((dsDbNode = datasetAttrMap.getNamedItem(DataSetConfig.DSDB)) != null)
	    			           && ((dsDataClassNode = datasetAttrMap.getNamedItem(DataSetConfig.DSDATACLASS))   != null)
	    			           && ((dsClassNode = datasetAttrMap.getNamedItem(DataSetConfig.DSCLASS)) != null)
	    			           && ((dsNameNode = datasetAttrMap.getNamedItem(DataSetConfig.DSNAME))   != null)
	    			           && ((dsIsCacheNode = datasetAttrMap.getNamedItem(DataSetConfig.DSISCACHE)) != null)
	    			           && ((dsDb = dsDbNode.getNodeValue()) != null)
	    			           && ((dsDataClass   = dsDataClassNode.getNodeValue())   != null)
	    			           && ((dsClass = dsClassNode.getNodeValue()) != null)
	    			           && ((dsName   = dsNameNode.getNodeValue())   != null)
	    			           && ((dsIsCache = dsIsCacheNode.getNodeValue()) != null)
	    			           )
	    			        {
	    		        		try 
	    		        		{	    		        			
	    		        			dataBase = this.getDatabase(dsDb);
	    		        			if(dataBase == null)
	    		        			{
	    		        				System.out.printf("Fail to find db(%s) in dataset(%s) configure \r\n",dsDb,dsDataClass);
	    		        				return 1;
	    		        			}  
	    		        			
	                                dataClass = this.getDataClass(packageName+dsDataClass);
	                                if(dataClass == null)
	                                {	    		        			
		    		        			dataClass = new Entity();
		    		        			if(dataClass.loadClass(packageName, dsDataClass) != 0)
		    		        			{
		    		        				System.out.printf("Fail to load data class %s.%s \r\n",packageName,dsDataClass);
		    		        				return 1;
		    		        			}
		    		        			this.addDataClass(dataClass);
	                                } 
	                                
	    		        			//创建数据集
	                                dataSet = (DataSet)Class.forName(DalManager.class.getPackage().getName()+"."+ dsClass).newInstance();
	                                
	                                //设置数据集对应的数据库
	                                dataSet.setDb(dataBase);
	                                dataSet.setMyClass(dataClass);
	                                dataSet.setName(dsName);	                               
	                                
	                                if(dsIsCache.equals("0"))
	                                {
	                                	 dataSet.setCache(false);
	                                	 this.addDataSet(packageName+"."+dsDataClass,dataSet);
	                                }
	                                else
	                                {
	                                	dataSet.setCache(true);
	                                	this.addDataSet(dsName,dataSet);
	                                }
                                }
                                catch (InstantiationException | IllegalAccessException
                                        | ClassNotFoundException e) 
                                {
	                                e.printStackTrace();
	                                return 1;
                                }
	
	    			        }
	    			        else
	    			        {
    			        		System.out.printf("Attribute lost in node %s \r\n",confNodeName);  			   
    			        		return 1;
	    			        } 	        				
	        				
	        				subNodes = confChildNode.getChildNodes();	
	        				for(int j=0;j<subNodes.getLength();j++)
	        				{
	        					confsubNode = subNodes.item(j);
			        			if(confsubNode.getNodeType() != Node.ELEMENT_NODE)
			    	        	{
			    	        		continue;
			    	        	}
			        			if(confsubNode.getNodeName().equals(DataSetConfig.COMTEMPLATES))
			        			{
			        				sub2Nodes = confsubNode.getChildNodes();	
			        				for(int k=0;k<sub2Nodes.getLength();k++)
			        				{
			        					confsub2Node = sub2Nodes.item(k);
					        			if(confsub2Node.getNodeType() != Node.ELEMENT_NODE)
					    	        	{
					    	        		continue;
					    	        	}
					        			if(confsub2Node.getNodeName().equals(DataSetConfig.COMTEMPLATE))
					        			{
					    		        	NamedNodeMap ctAttrMap = confsub2Node.getAttributes();
					    		        	Node ctNameNode;
					    		        	Node ctTypeNode;
					    		        	String ctName;
					    		        	String ctType;
					    		        	int ctTypeInt;

					    		        	if((ctAttrMap != null)
					    			           && ((ctNameNode = ctAttrMap.getNamedItem(DataSetConfig.COMTEMPLATENAME)) != null)
					    			           && ((ctTypeNode = ctAttrMap.getNamedItem(DataSetConfig.COMTEMPLATETYPE))   != null)
					    			           && ((ctName = ctNameNode.getNodeValue()) != null)
					    			           && ((ctType   = ctTypeNode.getNodeValue())!= null)
					    			           )
					    			        {
					    		        		//创建操作命令模版
					    		  				CommandTemplate template = new CommandTemplate();
					    		        		
						        				sub3Nodes = confsub2Node.getChildNodes();	
						        				for(int l=0;l<sub3Nodes.getLength();l++)
						        				{
						        					confsub3Node = sub3Nodes.item(l);
								        			if(confsub3Node.getNodeType() != Node.ELEMENT_NODE)
								    	        	{
								    	        		continue;
								    	        	}
								        			if(confsub3Node.getNodeName().equals(DataSetConfig.FORMAT))
								        			{
								      		        	NamedNodeMap ftAttrMap = confsub3Node.getAttributes();
								    		        	Node ftNameNode;
								    		        	Node ftParaNumNode;
								    		        	String ftName;
								    		        	String ftParaNum;
								    		        	if((ftAttrMap != null)
										    			   && ((ftNameNode = ftAttrMap.getNamedItem(DataSetConfig.FORMATNAME)) != null)
										    			   && ((ftParaNumNode = ftAttrMap.getNamedItem(DataSetConfig.FORMATPARANUM)) != null)
										    			   && ((ftName = ftNameNode.getNodeValue()) != null)
										    			   && ((ftParaNum = ftParaNumNode.getNodeValue())   != null)
										    			   )
								    		        	{
								    		        		Formater format = new Formater(ftName, confsub3Node.getTextContent(), Integer.parseInt(ftParaNum));
								    		        		if(template.addFormat(format) != 0)
								    		        		{
								    		        			System.out.printf("Fail to add command template %s's format \r\n",ctName,ftName);
								    		        			Logger.warn("Fail to add command template %s's format", ctName,ftName);
									        					return 1;	    			
								    		        		}
								        			
								    		        	}
								    		        	else
								    		        	{
								    		        		System.out.printf("Fail to add command template %s, type %s format lost attribute\r\n",ctName,ctType);
								    		        		Logger.warn("Fail to add command template %s, type %s format lost attribute", ctName,ctType);
								    		        		return 1;
								    		        	}
								        			}
						        				}						        				
						      
						        				template.setName(ctName);
						        				ctTypeInt = CommandType.nameToType(ctType);
						        				if(ctTypeInt < 0)
						        				{
						        					System.out.printf("Fail to add command template %s, type %s not supported \r\n",ctName,ctType);
						        					Logger.warn("Fail to add command template %s, type %s not supported", ctName,ctType);
						        					return 1;						        					
						        				}
						        				template.setType(ctTypeInt);
						        				if(dataSet.addCmdTemplate(template) != 0)
						        				{
						        					System.out.printf("Fail to add command template %s, name may be duplicated? \r\n",ctName);
						        					Logger.warn("Fail to add command template %s, name may be duplicated?", ctName);
						        					return 1;
						        				}
					    			        }
					    			        else
					    			        {
				    			        		System.out.printf("Attribute lost in node %s \r\n",confsub2Node.getNodeName());
				    			        		Logger.warn("Attribute lost in node %s", confsub2Node.getNodeName());
				    			        		return 1;
					    			        } 	        									        				
					        			}
					        			
			        				}
			        			}
			        			else
			        			{
			        				System.out.printf("configure file has no supportted node %s \r\n",confsubNode.getNodeName());
			        				Logger.warn("configure file has no supportted node", confsubNode.getNodeName());
			        				return 1;			        				
			        			}
		        			}
	        			}
	        			else
	        			{
	        				System.out.printf("configure file has no supportted node %s \r\n",confChildNode.getNodeName());
	        				Logger.warn("configure file has no supportted node", confChildNode.getNodeName());
	        				return 1;
	        			}        			
	        		}
	        	}
	        	else
	        	{
	        		System.out.printf("configure file has no supportted node %s \r\n",confNodeName);
	        		Logger.warn("configure file has no supportted node", confNodeName);
	        		return 1;
	        	}
	        } 
	        
	        return 0;
        }
        catch (ParserConfigurationException |SAXException | IOException e)
        {
        	Logger.error("Fail to load dataset %s", e);
        	return 1;
        }
    }
	
    /**
     * 
     * 准备工作  
     *    
     * @return
     */
    private int toBeReady()
	{
		int ret = 0;
		Iterator<Entry<String, ClustClient>> iter = this.clients.entrySet().iterator();

		while (iter.hasNext())
		{
			Entry<String, ClustClient> entry = iter.next();
			ClustClient client = entry.getValue();
			if (client.toBeReady() != 0)
			{
				System.out.printf("Client %s cannot be ready\r\n", client.getName());
				ret = 1;
			}
		}

		return ret;
	}
    
	public int close()
	{
		int ret = 0;
		Iterator<Entry<String, ClustClient>> iter = this.clients.entrySet().iterator();

		while (iter.hasNext()) 
		{
			Entry<String, ClustClient> entry = iter.next();
			ClustClient client = entry.getValue();
            ret = client.close();
		}
		return ret;
	}
	
	private static InputStream locateFromClasspath(String resourceName, boolean isAbsolutePath, Class<?> loader)
	{
		InputStream in = null;
		if(isAbsolutePath)
		{			
			try {
	            FileInputStream fis = new FileInputStream(resourceName);
	            long size = fis.getChannel().size();
	            byte[] buffer = new byte[(int) size];
	            fis.read(buffer, 0, (int) size);
	            fis.close();
	            
	            in = new ByteArrayInputStream(buffer);	            
            }
            catch (IOException e) {
	            e.printStackTrace();
	            in = null;
            }			
		}
		else
		{
			if (loader != null) 
			{				
				in = loader.getResourceAsStream(resourceName);
			}
			
			if(in == null)
			{
				ClassLoader tmpLoader = Thread.currentThread().getContextClassLoader();
				if(tmpLoader != null)
				{
					in = tmpLoader.getResourceAsStream(resourceName);
				}
			}
			if (in == null) 
			{
				in = ClassLoader.getSystemResourceAsStream(resourceName);
			}
		}
		return in;
	}
}
