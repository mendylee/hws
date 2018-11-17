package com.xrk.hws.dal.core;


/**
 * 
 * 类: 数所库服务器. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DBServer
{
	private int sid;
	// nosql必选属性
	private String name;
	private String host;
	private int port;
	
	// 公共属性
	private int policy;
	private int timeout;
	private int weight;
	
	// mysql必须属性
	private String url;
	private String driver;
	private String user;
	private String pass;
	private String schema;
	
	private int maxSize;
	private int minSize;
	private int idleConnection;
	private boolean fsync = false;
	
	/** 关系型数据库*/
	public static class NosqlServer
	{
		public final static int READ_ONLY 		= 1;
		public final static int WRITE_ONLY		= 2;
		public final static int READ_WRITE		= 3;
		
		// Required parameters.
		private String name;
		private final String host;
		private final int port;
		
		// Optional parameters.initialized to default value
		private int sid;
		private int policy;			// 读写策略，1，只读，2，只写，3，读写
		private String user;	
		private String pass;
		private String schema;
		private int timeout = 1000;
		private int weight = 0;
		private boolean fsync = false;	// 数据一致性策略
		
		public NosqlServer(final String host,final int port)
		{
			this.host = host;
			this.port = port;
		}
		
		public NosqlServer(final String host,final int port,final String name)
		{
			this.host = host;
			this.port = port;
			this.name = name;
			this.policy(READ_ONLY);
		}
		
		public NosqlServer sid(final int sid)
		{
			this.sid = sid;
			return this;
		}
		
		public NosqlServer policy(int policy)
		{
			this.policy = policy;
			return this;
		}
		
		public NosqlServer user(final String user)
		{
			this.user = user;
			return this;
		}
		
		public NosqlServer schema(final String schema)
		{
			this.schema = schema;
			return this;
		}
		
		public NosqlServer pass(final String pass)
		{
			this.pass = pass;
			return this;
		}
		
		public NosqlServer timeout(final int timeout)
		{
			this.timeout = timeout;
			return this;
		}
		
		public NosqlServer weight(final int weight)
		{
			this.weight = weight;
			return this;
		}
		
		public NosqlServer fsync(final boolean fsync)
		{
			this.fsync = fsync;
			return this;
		}
		
		public DBServer build()
		{
			return new DBServer(this);
		}
	}
	
	public static class MysqlServer
	{
		// Required parameters.
		private final String url;
		private final String driver;
		private final String user;
		private final String pass;
		
		// Optional parameters.initialized to default value
		private int sid;
		private int timeout = 10000;
		private int maxSize = 5;
		private int minSize = 5;
		private int idleConnection = 10;
		
		public MysqlServer(String url,String driver,String user,String pass)
		{
			this.url = url;
			this.driver = driver;
			this.user = user;
			this.pass = pass;
		}
		
		public MysqlServer sid(int sid)
		{
			this.sid = sid;
			return this;
		}
		
		public MysqlServer timeout(int timeout)
		{
			this.timeout = timeout;
			return this;
		}
		
		public MysqlServer maxSize(int maxSize)
		{
			this.maxSize = maxSize;
			return this;
		}
		
		public MysqlServer minSize(int minSize)
		{
			this.minSize = minSize;
			return this;
		}
		
		public MysqlServer idleConnection(int idleConnection)
		{
			this.idleConnection = idleConnection;
			return this;
		}
		
		public DBServer build()
		{
			return new DBServer(this);
		}
	}
	
	public DBServer(NosqlServer builder)
	{
		this.name = builder.name;
		this.host = builder.host;
		this.port = builder.port;
		
		this.sid  	 = builder.sid;
		this.policy  = builder.policy;
		this.user 	 = builder.user;
		this.schema	 = builder.schema;
		this.pass 	 = builder.pass;
		this.timeout = builder.timeout;
		this.weight  = builder.weight;
		this.fsync   = builder.fsync;
	}
	
	public DBServer(MysqlServer builder)
	{
		this.sid  = builder.sid;
		
		this.url = builder.url;
		this.driver = builder.driver;
		this.user = builder.user;
		this.pass = builder.pass;
		
		this.timeout = builder.timeout;
		this.maxSize = builder.maxSize;
		this.minSize = builder.minSize;
		this.idleConnection = builder.idleConnection;
	}

	public int getSid()
	{
		return sid;
	}

	public void setSid(int sid)
	{
		this.sid = sid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getPolicy()
	{
		return policy;
	}

	public void setPolicy(int policy)
	{
		this.policy = policy;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public int getWeight()
	{
		return weight;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPass()
	{
		return pass;
	}

	public void setPass(String pass)
	{
		this.pass = pass;
	}

	public String getSchema()
	{
		return schema;
	}

	public void setSchema(String schema)
	{
		this.schema = schema;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	public int getMinSize()
	{
		return minSize;
	}

	public void setMinSize(int minSize)
	{
		this.minSize = minSize;
	}

	public int getIdleConnection()
	{
		return idleConnection;
	}

	public void setIdleConnection(int idleConnection)
	{
		this.idleConnection = idleConnection;
	}

	public boolean isFsync()
	{
		return fsync;
	}

	public void setFsync(boolean fsync)
	{
		this.fsync = fsync;
	}

	
}
