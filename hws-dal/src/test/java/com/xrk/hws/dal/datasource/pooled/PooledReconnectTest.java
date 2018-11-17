package com.xrk.hws.dal.datasource.pooled;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * PooledReconnectTest: PooledReconnectTest.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月5日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PooledReconnectTest 
{
	private static PooledDataSource dataSource = null;
	private static Connection connection = null;
	
	public static void main(String[] args)
	{
		dataSource = new PooledDataSource("org.postgresql.Driver","jdbc:postgresql://192.168.8.22:5432/bxr_app_production", "xrkadmin", "xrktest123");
		// 数据库侦听检测
		dataSource.setPoolPingEnabled(true);
		dataSource.setPoolPingConnectionsNotUsedFor(20000);
		dataSource.setPoolPingQuery("select 1");
		try 
		{
			connection = dataSource.getConnection();
			System.out.println("~~~~~~~~~~~~~~~~~正常连接~~~~~~~~~~~~~~");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		//=========================断开网络连接========================
		try 
		{
			Thread.sleep(10000);
			connection = dataSource.getConnection();
		} 
		catch (InterruptedException | SQLException e) 
		{
			System.out.println("--------------断开连接-----------------" + e.getMessage());
		}
		
		//=========================重新建立网络连接========================
		try 
		{
			Thread.sleep(10000);
			System.out.println("正在尝试恢复连接，请稍后...");
			connection = dataSource.getConnection();
		} 
		catch (InterruptedException | SQLException e) 
		{
			System.out.println("恢复连接失败！");
			e.printStackTrace();
		}
		System.out.println("****************恢复连接**********************");
	}
}
