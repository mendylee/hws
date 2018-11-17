package com.xrk.hws.dal.datasource.unpooled;

import static org.junit.Assert.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.junit.Ignore;
import org.junit.Test;

/**
 * UnpooledDataSourceTest: UnpooledDataSourceTest.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月5日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UnpooledDataSourceTest
{
	@Test
	public void shouldNotRegisterTheSameDriverMultipleTimes() throws Exception
	{
		UnpooledDataSource dataSource = null;
		dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver","jdbc:hsqldb:mem:multipledrivers", "sa", "");
		dataSource.getConnection();
		int before = countRegisteredDrivers();
		dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver","jdbc:hsqldb:mem:multipledrivers", "sa", "");
		dataSource.getConnection();
		assertEquals(before, countRegisteredDrivers());
	}
	
	@Ignore("Requires MySQL server and a driver.")
	@Test
	public void shouldRegisterDynamicallyLoadedDriver() throws Exception
	{
		int before = countRegisteredDrivers();
		ClassLoader driverClassLoader = null;
		UnpooledDataSource dataSource = null;
		driverClassLoader = new URLClassLoader(new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.35.jar!/") });
		dataSource = new UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver","jdbc:mysql://192.168.9.109/db1", "root", "");
		dataSource.getConnection();
		assertEquals(before + 1, countRegisteredDrivers());
		driverClassLoader = new URLClassLoader(
		                                       new URL[] { new URL("jar:file:/PATH_TO/mysql-connector-java-5.1.35.jar!/") });
		dataSource = new UnpooledDataSource(driverClassLoader, "com.mysql.jdbc.Driver","jdbc:mysql://192.168.9.109/db1", "root", "");
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
