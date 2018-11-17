package com.xrk.hws.dal.datasource.pooled;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.datasource.unpooled.UnpooledDataSource;

/**
 * 类: 带连接沲的数据源.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PooledDataSource implements DataSource
{
	/**
	 * 连接沲状态.
	 */
	private final PoolState state = new PoolState(this);

	private final UnpooledDataSource dataSource;
	
	// 数据源配置可选项.
	protected int poolMaximumActiveConnections = 10;		// 在任意时间可以存在的活动（也就是正在使用）连接数量，默认值：10.
	protected int poolMaximumIdleConnections = 5;			// 任意时间可能存在的空闲连接数.
	protected int poolMaximumCheckoutTime = 20000;			// 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）.
	protected int poolTimeToWait = 20000;					// 这是一个底层设置，如果获取连接花费的相当长的时间，它会给连接池打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直安静的失败），默认值：20000 毫秒（即 20 秒）.
	protected String poolPingQuery = "NO PING QUERY SET";	// 发送到数据库的侦测查询，用来检验连接是否处在正常工作秩序中并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动失败时带有一个恰当的错误消息.
	protected boolean poolPingEnabled = false;				// 是否启用侦测查询。若开启，也必须使用一个可执行的 SQL 语句设置 poolPingQuery 属性（最好是一个非常快的 SQL），默认值：false.
	protected int poolPingConnectionsNotUsedFor = 0;		// 配置 poolPingQuery的使用频度。这可以被设置成匹配具体的数据库连接超时时间，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。
	
	private int expectedConnectionTypeCode;
	
	public PooledDataSource() 
	{
		dataSource = new UnpooledDataSource();
	}

	public PooledDataSource(UnpooledDataSource dataSource) 
	{
		this.dataSource = dataSource;
	}

	public PooledDataSource(String driver, String jdbcUrl, String username, String password) 
	{
		dataSource = new UnpooledDataSource(driver, jdbcUrl, username, password);
		expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getJdbcUrl(),dataSource.getUsername(), dataSource.getPassword());
	}

	public PooledDataSource(String driver, String jdbcUrl, Properties driverProperties) 
	{
		dataSource = new UnpooledDataSource(driver, jdbcUrl, driverProperties);
		expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getJdbcUrl(),dataSource.getUsername(), dataSource.getPassword());
	}

	public PooledDataSource(ClassLoader driverClassLoader, String driver, String jdbcUrl,String username, String password) 
	{
		dataSource = new UnpooledDataSource(driverClassLoader, driver, jdbcUrl, username, password);
		expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getJdbcUrl(),dataSource.getUsername(), dataSource.getPassword());
	}

	public PooledDataSource(ClassLoader driverClassLoader, String driver, String jdbcUrl,Properties driverProperties) 
	{
		dataSource = new UnpooledDataSource(driverClassLoader, driver, jdbcUrl, driverProperties);
		expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getJdbcUrl(), dataSource.getUsername(), dataSource.getPassword());
	}

	@Override
	public Connection getConnection() throws SQLException
	{
		return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException
	{
		return popConnection(username, password).getProxyConnection();
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

	public void setDriver(String driver)
	{
		dataSource.setDriver(driver);
		forceCloseAll();
	}

	public void setJdbcUrl(String jdbcUrl)
	{
		dataSource.setJdbcUrl(jdbcUrl);
		forceCloseAll();
	}

	public void setUsername(String username)
	{
		dataSource.setUsername(username);
		forceCloseAll();
	}

	public void setPassword(String password)
	{
		dataSource.setPassword(password);
		forceCloseAll();
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit)
	{
		dataSource.setAutoCommit(defaultAutoCommit);
		forceCloseAll();
	}

	public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel)
	{
		dataSource.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
		forceCloseAll();
	}

	public void setDriverProperties(Properties driverProps)
	{
		dataSource.setDriverProperties(driverProps);
		forceCloseAll();
	}

	/*
	 * The maximum number of active connections
	 * 
	 * @param poolMaximumActiveConnections The maximum number of active
	 * connections
	 */
	public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections)
	{
		this.poolMaximumActiveConnections = poolMaximumActiveConnections;
		forceCloseAll();
	}

	/*
	 * The maximum time a connection can be used before it *may* be given away
	 * again.
	 * 
	 * @param poolMaximumCheckoutTime The maximum time
	 */
	public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime)
	{
		this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
		forceCloseAll();
	}

	/*
	 * The time to wait before retrying to get a connection
	 * 
	 * @param poolTimeToWait The time to wait
	 */
	public void setPoolTimeToWait(int poolTimeToWait)
	{
		this.poolTimeToWait = poolTimeToWait;
		forceCloseAll();
	}

	/*
	 * The query to be used to check a connection
	 * 
	 * @param poolPingQuery The query
	 */
	public void setPoolPingQuery(String poolPingQuery)
	{
		this.poolPingQuery = poolPingQuery;
		forceCloseAll();
	}

	/*
	 * The maximum number of idle connections
	 * 
	 * @param poolMaximumIdleConnections The maximum number of idle connections
	 */
	public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections)
	{
		this.poolMaximumIdleConnections = poolMaximumIdleConnections;
		forceCloseAll();
	}

	/*
	 * Determines if the ping query should be used.
	 * 
	 * @param poolPingEnabled True if we need to check a connection before using
	 * it
	 */
	public void setPoolPingEnabled(boolean poolPingEnabled)
	{
		this.poolPingEnabled = poolPingEnabled;
		forceCloseAll();
	}

	/*
	 * If a connection has not been used in this many milliseconds, ping the
	 * database to make sure the connection is still good.
	 * 
	 * @param milliseconds the number of milliseconds of inactivity that will
	 * trigger a ping
	 */
	public void setPoolPingConnectionsNotUsedFor(int milliseconds)
	{
		this.poolPingConnectionsNotUsedFor = milliseconds;
		forceCloseAll();
	}

	public String getDriver()
	{
		return dataSource.getDriver();
	}

	public String getJdbcUrl()
	{
		return dataSource.getJdbcUrl();
	}

	public String getUsername()
	{
		return dataSource.getUsername();
	}

	public String getPassword()
	{
		return dataSource.getPassword();
	}

	public boolean isAutoCommit()
	{
		return dataSource.isAutoCommit();
	}

	public Integer getDefaultTransactionIsolationLevel()
	{
		return dataSource.getDefaultTransactionIsolationLevel();
	}

	public Properties getDriverProperties()
	{
		return dataSource.getDriverProperties();
	}

	public int getPoolMaximumActiveConnections()
	{
		return poolMaximumActiveConnections;
	}

	public int getPoolMaximumIdleConnections()
	{
		return poolMaximumIdleConnections;
	}

	public int getPoolMaximumCheckoutTime()
	{
		return poolMaximumCheckoutTime;
	}

	public int getPoolTimeToWait()
	{
		return poolTimeToWait;
	}

	public String getPoolPingQuery()
	{
		return poolPingQuery;
	}

	public boolean isPoolPingEnabled()
	{
		return poolPingEnabled;
	}

	public int getPoolPingConnectionsNotUsedFor()
	{
		return poolPingConnectionsNotUsedFor;
	}

	/** 
	 * Closes all active and idle connections in the pool
	 */
	public void forceCloseAll()
	{
		synchronized (state) 
		{
			expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getJdbcUrl(),dataSource.getUsername(), dataSource.getPassword());
			for (int i = state.activeConnections.size(); i > 0; i--) 
			{
				try 
				{
					PooledConnection conn = (PooledConnection) state.activeConnections.remove(i - 1);
					conn.invalidate();

					Connection realConn = conn.getRealConnection();
					if (!realConn.getAutoCommit()) 
					{
						realConn.rollback();
					}
					realConn.close();
				}
				catch (Exception e) 
				{
					// ignore
				}
			}
			for (int i = state.idleConnections.size(); i > 0; i--) 
			{
				try 
				{
					PooledConnection conn = (PooledConnection) state.idleConnections.remove(i - 1);
					conn.invalidate();

					Connection realConn = conn.getRealConnection();
					if (!realConn.getAutoCommit()) 
					{
						realConn.rollback();
					}
					realConn.close();
				}
				catch (Exception e) 
				{
					// ignore
				}
			}
		}
		if (Logger.isDebugEnabled()) 
		{
			Logger.debug("PooledDataSource forcefully closed/removed all connections.");
		}
	}

	public PoolState getPoolState()
	{
		return state;
	}

	private int assembleConnectionTypeCode(String url, String username, String password)
	{
		return ("" + url + username + password).hashCode();
	}

	protected void pushConnection(PooledConnection conn) throws SQLException
	{

		synchronized (state) 
		{
			state.activeConnections.remove(conn);
			if (conn.isValid()) 
			{
				if (state.idleConnections.size() < poolMaximumIdleConnections && conn.getConnectionTypeCode() == expectedConnectionTypeCode) 
				{
					state.accumulatedCheckoutTime += conn.getCheckoutTime();
					if (!conn.getRealConnection().getAutoCommit()) 
					{
						conn.getRealConnection().rollback();
					}
					PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
					state.idleConnections.add(newConn);
					newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
					newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
					conn.invalidate();
					if (Logger.isDebugEnabled()) 
					{
						Logger.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
					}
					state.notifyAll();
				}
				else 
				{
					state.accumulatedCheckoutTime += conn.getCheckoutTime();
					if (!conn.getRealConnection().getAutoCommit()) 
					{
						conn.getRealConnection().rollback();
					}
					conn.getRealConnection().close();
					if (Logger.isDebugEnabled()) 
					{
						Logger.debug("Closed connection " + conn.getRealHashCode() + ".");
					}
					conn.invalidate();
				}
			}
			else 
			{
				if (Logger.isDebugEnabled()) 
				{
					Logger.debug("A bad connection (" + conn.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
				}
				state.badConnectionCount++;
			}
		}
	}

	/**
	 * 从连接池中返回一个可用的PooledConnection对象.  
	 *    
	 * @param username		用户名.
	 * @param password		用户密码.
	 * @return
	 * @throws SQLException
	 */
	private PooledConnection popConnection(String username, String password) throws SQLException
	{
		boolean countedWait = false;
		PooledConnection conn = null;
		long t = System.currentTimeMillis();
		int localBadConnectionCount = 0;

		while (conn == null) 
		{
			synchronized (state) 
			{
				// 1.先看是否有空闲(idle)状态下的PooledConnection对象,
				// 如果有，就直接返回一个可用的PooledConnection对象；否则进行第2步。
				if (!state.idleConnections.isEmpty()) 
				{
					conn = state.idleConnections.remove(0);
					if (Logger.isDebugEnabled()) 
					{
						Logger.debug("Checked out connection " + conn.getRealHashCode()+ " from pool.");
					}
				}
				else 
				{
					// 2.查看活动状态的PooledConnection沲activeConnection是否已满;
					// 如果没有满，则创建一个新的PooledConnection对象，然后放到activeConnections沲中，
					// 然后返回PooledConnection对象;否则进入第三步
					if (state.activeConnections.size() < poolMaximumActiveConnections) 
					{
						conn = new PooledConnection(dataSource.getConnection(), this);
						if (Logger.isDebugEnabled()) 
						{
							Logger.debug("Created connection " + conn.getRealHashCode() + ".");
						}
					}
					else 
					{
						// 3.看最先进入activeConnections池中的PooledConnection对象是否已经过期：
						// 如果已经过期，从activeConnections池中移除此对象，
						// 然后创建一个新的PooledConnection对象，
						// 添加到activeConnections中，然后将此对象返回；否则进行第4步。
						PooledConnection oldestActiveConnection = state.activeConnections.get(0);
						long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
						if (longestCheckoutTime > poolMaximumCheckoutTime) 
						{
							// 3.1累计已经过期的连接数
							state.claimedOverdueConnectionCount++;
							// 3.2累计超时和过期的连接数
							state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
							// 3.3累计连接超时数
							state.accumulatedCheckoutTime += longestCheckoutTime;
							// 3.4从activeConnection沲中移除
							state.activeConnections.remove(oldestActiveConnection);
							// 3.5判断过期连接是否是自动提交，撤消对当前事务中所做的所有更改和任何数据库当前持有锁的释放
							if (!oldestActiveConnection.getRealConnection().getAutoCommit()) 
							{
								oldestActiveConnection.getRealConnection().rollback();
							}
							// 3.6创建一个新的PooledConnection对象
							conn = new PooledConnection(oldestActiveConnection.getRealConnection(),this);
							// 3.7校验连接是否可用.
							oldestActiveConnection.invalidate();
							if (Logger.isDebugEnabled()) 
							{
								Logger.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
							}
						}
						else 
						{
							// 必须等待
							try 
							{
								if (!countedWait) 
								{
									state.hadToWaitCount++;
									countedWait = true;
								}
								if (Logger.isDebugEnabled()) 
								{
									Logger.debug("Waiting as long as " + poolTimeToWait+ " milliseconds for connection.");
								}
								long wt = System.currentTimeMillis();
								state.wait(poolTimeToWait);
								state.accumulatedWaitTime += System.currentTimeMillis() - wt;
							}
							catch (InterruptedException e) 
							{
								break;
							}
						}
					}
				}
				if (conn != null) 
				{
					// 判断连接是否可用
					if (conn.isValid()) 
					{
						if (!conn.getRealConnection().getAutoCommit()) 
						{
							conn.getRealConnection().rollback();
						}
						// 设置连接类型hash值，code=(url+username+password).hashCode()
						conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getJdbcUrl(),username, password));
						// 设置检测超时时间，默认为当前系统时间
						conn.setCheckoutTimestamp(System.currentTimeMillis());
						// 设置最后一次使用连接的时间，默认获取系统的最近一次时间
						conn.setLastUsedTimestamp(System.currentTimeMillis());
						// 添加到activeConnections中，然后将此对象返回
						state.activeConnections.add(conn);
						// 累计请求数.
						state.requestCount++;
						// 累计请求时间，accumnlatedRequestTime = accumnlatedRequestTime + (System.currentTimeMillis() - t)
						state.accumulatedRequestTime += System.currentTimeMillis() - t;
					}
					else 
					{
						if (Logger.isDebugEnabled()) 
						{
							Logger.debug("A bad connection (" + conn.getRealHashCode()+ ") was returned from the pool, getting another connection.");
						}
						// 累计不可用的连接数.
						state.badConnectionCount++;
						// 累计当前不可用的连接数.
						localBadConnectionCount++;
						conn = null;
						if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) 
						{
							if (Logger.isDebugEnabled()) 
							{
								Logger.debug("PooledDataSource: Could not get a good connection to the database.");
							}
							throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
						}
					}
				}
			}

		}

		if (conn == null) 
		{
			if (Logger.isDebugEnabled()) 
			{
				Logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
			}
			throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
		}

		return conn;
	}

	/*
	 * Method to check to see if a connection is still usable
	 * 
	 * @param conn - the connection to check
	 * 
	 * @return True if the connection is still usable
	 */
	protected boolean pingConnection(PooledConnection conn)
	{
		boolean result = true;
		try 
		{
			result = !conn.getRealConnection().isClosed();
		}
		catch (SQLException e) 
		{
			if (Logger.isDebugEnabled()) 
			{
				Logger.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
			}
			result = false;
		}

		if (result) 
		{
			if (poolPingEnabled) 
			{
				if (poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) 
				{
					try 
					{
						if (Logger.isDebugEnabled()) 
						{
							Logger.debug("Testing connection " + conn.getRealHashCode() + " ...");
						}
						Connection realConn = conn.getRealConnection();
						Statement statement = realConn.createStatement();
						ResultSet rs = statement.executeQuery(poolPingQuery);
						rs.close();
						statement.close();
						if (!realConn.getAutoCommit()) 
						{
							realConn.rollback();
						}
						result = true;
						if (Logger.isDebugEnabled()) 
						{
							Logger.debug("Connection " + conn.getRealHashCode() + " is GOOD!");
						}
					}
					catch (Exception e) 
					{
						Logger.warn("Execution of ping query '" + poolPingQuery + "' failed: "+ e.getMessage());
						try 
						{
							conn.getRealConnection().close();
						}
						catch (Exception e2) {
							// ignore
						}
						result = false;
						if (Logger.isDebugEnabled()) 
						{
							Logger.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
						}
					}
				}
			}
		}
		return result;
	}

	/*
	 * Unwraps a pooled connection to get to the 'real' connection
	 * 
	 * @param conn - the pooled connection to unwrap
	 * 
	 * @return The 'real' connection
	 */
	public static Connection unwrapConnection(Connection conn)
	{
		if (Proxy.isProxyClass(conn.getClass())) 
		{
			InvocationHandler handler = Proxy.getInvocationHandler(conn);
			if (handler instanceof PooledConnection) 
			{
				return ((PooledConnection) handler).getRealConnection();
			}
		}
		return conn;
	}

	protected void finalize() throws Throwable
	{
		forceCloseAll();
		super.finalize();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		throw new SQLException(getClass().getName() + " is not a wrapper.");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return false;
	}

	@Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
	    return null;
    }

}
