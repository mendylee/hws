package com.xrk.hws.dal.datasource.pooled;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.junit.Ignore;
import org.junit.Test;

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
public class PooledDataSourceTest 
{
	@Test
	public void shouldNotRegisterTheSameDriverMultipleTimes() throws Exception
	{
		PooledDataSource dataSource = null;
		Connection connection = null;
		
		dataSource = new PooledDataSource("org.postgresql.Driver","jdbc:postgresql://192.168.8.22:5432/bxr_app_production", "xrkadmin", "xrktest123");
		// 数据库侦听检测
		dataSource.setPoolPingEnabled(true);
		dataSource.setPoolPingConnectionsNotUsedFor(20000);
		dataSource.setPoolPingQuery("select 1");
		connection = dataSource.getConnection();
		
		int before = countRegisteredDrivers();
//		dataSource = new PooledDataSource("org.postgresql.Driver","jdbc:postgresql://192.168.8.22:5432/bxr_app_production", "xrkadmin", "xrktest123");
//		// 数据库侦听检测
//		dataSource.setPoolPingEnabled(true);
//		dataSource.setPoolPingConnectionsNotUsedFor(20000);
//		dataSource.setPoolPingQuery("select 1");
		connection = dataSource.getConnection();
		
		assertEquals(before, countRegisteredDrivers());
	}
	
	@Ignore("Requires MySQL server and a driver.")
	@Test
	public void shouldRegisterDynamicallyLoadedDriver() throws Exception
	{
		int before = countRegisteredDrivers();
		ClassLoader driverClassLoader = null;
		PooledDataSource dataSource = null;
		driverClassLoader = new URLClassLoader(new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.35.jar!/") });
		dataSource = new PooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver","jdbc:mysql://192.168.9.109/db1", "root", "");
		// 数据库侦听检测
		dataSource.setPoolPingEnabled(true);
		dataSource.setPoolPingConnectionsNotUsedFor(20000);
		dataSource.setPoolPingQuery("select 1");
		
		dataSource.getConnection();
		assertEquals(before + 1, countRegisteredDrivers());
		driverClassLoader = new URLClassLoader(
		                                       new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.35.jar!/") });
		dataSource = new PooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver","jdbc:mysql://192.168.9.109/db1", "root", "");
		// 数据库侦听检测
		dataSource.setPoolPingEnabled(true);
		dataSource.setPoolPingConnectionsNotUsedFor(20000);
		dataSource.setPoolPingQuery("select 1");
		dataSource.getConnection();
		assertEquals(before + 1, countRegisteredDrivers());
	}
	
	/**
	 * 统计当前已注册的驱动连接数.  
	 *    
	 * @return	驱动数总和.
	 */
	protected int countRegisteredDrivers()
	{
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		int count = 0;
		while (drivers.hasMoreElements()) 
		{
			drivers.nextElement();
			count++;
		}
		return count;
	}
}
