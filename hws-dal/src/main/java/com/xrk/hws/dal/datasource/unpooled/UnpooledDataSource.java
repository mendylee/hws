package com.xrk.hws.dal.datasource.unpooled;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.xrk.hws.common.io.Resources;

/**
 * 类: 不使用连接沲的数据源.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UnpooledDataSource implements DataSource
{
	/**
	 * 连接驱动类加载器.
	 */
	private ClassLoader driverClassLoader;
	/**
	 * 连接驱动属性表.
	 */
	private Properties driverProperties;
	/**
	 * 连接驱动缓存，注册到内存中的驱动，以供后续使用.
	 */
	private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<String, Driver>();

	/**
	 * 数据库连接驱动.
	 */
	private String driver;
	/**
	 * 数据库连接url.
	 */
	private String jdbcUrl;
	/**
	 * 数据库连接用户名.
	 */
	private String username;
	/**
	 * 数据库连接密码.
	 */
	private String password;

	/**
	 * 设置是否自动提交.
	 */
	private Boolean autoCommit;
	/**
	 * 设置事务的隔离级别
	 * 1.Read Uncommitted 指定语句可以读取已由其他事务修改但尚未提交的行.最低等级的事务隔离，仅仅保证了读取过程中不会读取到非法数据.</br>
	 * 2.Read Committed：大多数主流数据库的默认事务等级，保证了一个事务不会读到另一个并行事务已修改但未提交的数据，避免了“脏读取”</br>
	 * 3.Repeatable Read：保证了一个事务不会修改已经由另一个事务读取但未提交（回滚）的数据。避免了“脏读取”和“不可重复读取”的情况，但是带来了更多的性能损失</br>
	 * 4.Serializable：最高等级的事务隔离，上面3种不确定情况都将被规避。这个级别将模拟事务的串行执行。</br>
	 */
	private Integer defaultTransactionIsolationLevel;
	
	static 
	{
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) 
		{
			Driver driver = drivers.nextElement();
			registeredDrivers.put(driver.getClass().getName(), driver);
		}
	}

	public UnpooledDataSource() 
	{
	}
	
	public UnpooledDataSource(String driver, String jdbcUrl, Properties driverProperties) 
	{
		this.driver = driver;
		this.jdbcUrl = jdbcUrl;
		this.driverProperties = driverProperties;
	}

	public UnpooledDataSource(String driver, String jdbcUrl, String username, String password) 
	{
		this.driver = driver;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}
	
	public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String jdbcUrl,Properties driverProperties) {
		this.driverClassLoader = driverClassLoader;
		this.driver = driver;
		this.jdbcUrl = jdbcUrl;
		this.driverProperties = driverProperties;
	}

	public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String jdbcUrl,String username, String password) 
	{
		this.driverClassLoader = driverClassLoader;
		this.driver = driver;
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public Connection getConnection() throws SQLException
	{
		return doGetConnection(username, password);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException
	{
		return doGetConnection(username, password);
	}

	@Override
	public void setLoginTimeout(int loginTimeout) throws SQLException
	{
		DriverManager.setLoginTimeout(loginTimeout);
	}

	@Override
	public int getLoginTimeout() throws SQLException
	{
		return DriverManager.getLoginTimeout();
	}

	@Override
	public void setLogWriter(PrintWriter logWriter) throws SQLException
	{
		DriverManager.setLogWriter(logWriter);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException
	{
		return DriverManager.getLogWriter();
	}

	public ClassLoader getDriverClassLoader()
	{
		return driverClassLoader;
	}

	public void setDriverClassLoader(ClassLoader driverClassLoader)
	{
		this.driverClassLoader = driverClassLoader;
	}

	public Properties getDriverProperties()
	{
		return driverProperties;
	}

	public void setDriverProperties(Properties driverProperties)
	{
		this.driverProperties = driverProperties;
	}

	public String getDriver()
	{
		return driver;
	}

	public synchronized void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getJdbcUrl()
	{
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl)
	{
		this.jdbcUrl = jdbcUrl;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Boolean isAutoCommit()
	{
		return autoCommit;
	}

	/**
	 * 设置是否自动提交事务.  
	 *    
	 * @param autoCommit	Boolean.valueOf(true|false).
	 */
	public void setAutoCommit(Boolean autoCommit)
	{
		this.autoCommit = autoCommit;
	}

	/**
	 * 获取默认的事务隔离级别.  
	 *    
	 * @return	事务隔离级别.
	 */
	public Integer getDefaultTransactionIsolationLevel()
	{
		return defaultTransactionIsolationLevel;
	}

	public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel)
	{
		this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
	}

	/**
	 * 获取数据库连接对象.  
	 *    
	 * @param username		用户名.		
	 * @param password		用户密码.
	 * @return				数据库连接实例.
	 * @throws SQLException
	 */
	private Connection doGetConnection(String username, String password) throws SQLException
	{
		Properties props = new Properties();
		if (driverProperties != null) {
			props.putAll(driverProperties);
		}
		if (username != null) {
			props.setProperty("user", username);
		}
		if (password != null) {
			props.setProperty("password", password);
		}
		return doGetConnection(props);
	}

	private Connection doGetConnection(Properties properties) throws SQLException
	{
		initializeDriver();
		Connection connection = DriverManager.getConnection(jdbcUrl, properties);
		configureConnection(connection);
		return connection;
	}

	/**
	 * 初始化数据库连接驱动.  
	 *    
	 * @throws SQLException
	 */
	private synchronized void initializeDriver() throws SQLException
	{
		if (!registeredDrivers.containsKey(driver)) 
		{
			Class<?> driverType;
			try 
			{
				if (driverClassLoader != null) 
				{
					driverType = Class.forName(driver, true, driverClassLoader);
				}
				else 
				{
					driverType = Resources.classForName(driver);
				}
				Driver driverInstance = (Driver) driverType.newInstance();
				DriverManager.registerDriver(new DriverProxy(driverInstance));
				registeredDrivers.put(driver, driverInstance);
			}
			catch (Exception e) 
			{
				throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
			}
		}
	}

	/**
	 * 配置连接参数选项.
	 * 
	 * 1.设置是否自动提交事务.</br>
	 * 2.设置事务隔离级别.</br>
	 *    
	 * @param conn				数据库连接对象.
	 * @throws SQLException
	 */
	private void configureConnection(Connection conn) throws SQLException
	{
		if (autoCommit != null && autoCommit != conn.getAutoCommit()) 
		{
			conn.setAutoCommit(autoCommit);
		}
		if (defaultTransactionIsolationLevel != null) 
		{
			conn.setTransactionIsolation(defaultTransactionIsolationLevel);
		}
	}

	/**
	 * 连接驱动代理.
	 */
	private static class DriverProxy implements Driver
	{
		private Driver driver;

		DriverProxy(Driver d) 
		{
			this.driver = d;
		}

		@Override
		public boolean acceptsURL(String u) throws SQLException
		{
			return this.driver.acceptsURL(u);
		}

		@Override
		public Connection connect(String u, Properties p) throws SQLException
		{
			return this.driver.connect(u, p);
		}

		@Override
		public int getMajorVersion()
		{
			return this.driver.getMajorVersion();
		}

		@Override
		public int getMinorVersion()
		{
			return this.driver.getMinorVersion();
		}

		@Override
		public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException
		{
			return this.driver.getPropertyInfo(u, p);
		}

		@Override
		public boolean jdbcCompliant()
		{
			return this.driver.jdbcCompliant();
		}

		public Logger getParentLogger()
		{
			return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		throw new SQLException(getClass().getName() + " is not a wrapper.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return false;
	}

	public Logger getParentLogger()
	{
		return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	}

}
