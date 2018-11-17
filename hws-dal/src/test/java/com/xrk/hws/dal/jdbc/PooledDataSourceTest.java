package com.xrk.hws.dal.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hsqldb.jdbc.JDBCConnection;
import org.junit.Test;

import com.xrk.hws.dal.BaseDataTest;
import com.xrk.hws.dal.datasource.pooled.PooledDataSource;

/**
 * PooledDataSourceTest: PooledDataSourceTest.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月5日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PooledDataSourceTest extends BaseDataTest
{
	@Test
	public void shouldProperlyMaintainPoolOf3ActiveAnd2IdleConnections() throws Exception
	{
		PooledDataSource ds = createPooledDataSource(JPETSTORE_PROPERTIES);
		try 
		{
			runScript(ds, JPETSTORE_DDL);
			ds.setDefaultAutoCommit(false);
			ds.setDriverProperties(new Properties() {
				{
					setProperty("username", "sa");
					setProperty("password", "");
				}
			});
			ds.setPoolMaximumActiveConnections(3);
			ds.setPoolMaximumIdleConnections(2);
			ds.setPoolMaximumCheckoutTime(10000);
			ds.setPoolPingConnectionsNotUsedFor(1);
			ds.setPoolPingEnabled(true);
			ds.setPoolPingQuery("SELECT * FROM PRODUCT");
			ds.setPoolTimeToWait(10000);
			ds.setLogWriter(null);
			List<Connection> connections = new ArrayList<Connection>();
			for (int i = 0; i < 3; i++) {
				connections.add(ds.getConnection());
			}
			assertEquals(3, ds.getPoolState().getActiveConnectionCount());
			for (Connection c : connections) {
				c.close();
			}
			assertEquals(2, ds.getPoolState().getIdleConnectionCount());
			assertEquals(4, ds.getPoolState().getRequestCount());
			assertEquals(0, ds.getPoolState().getBadConnectionCount());
			assertEquals(0, ds.getPoolState().getHadToWaitCount());
			assertEquals(0, ds.getPoolState().getAverageOverdueCheckoutTime());
			assertEquals(0, ds.getPoolState().getClaimedOverdueConnectionCount());
			assertEquals(0, ds.getPoolState().getAverageWaitTime());
			assertNotNull(ds.getPoolState().toString());
		}
		finally 
		{
			ds.forceCloseAll();
		}
	}

	@Test
	public void shouldNotFailCallingToStringOverAnInvalidConnection() throws Exception
	{
		PooledDataSource ds = createPooledDataSource(JPETSTORE_PROPERTIES);
		Connection c = ds.getConnection();
		c.close();
		c.toString();
	}

	@Test
	public void ShouldReturnRealConnection() throws Exception
	{
		PooledDataSource ds = createPooledDataSource(JPETSTORE_PROPERTIES);
		Connection c = ds.getConnection();
		JDBCConnection realConnection = (JDBCConnection) PooledDataSource.unwrapConnection(c);
	}
}
