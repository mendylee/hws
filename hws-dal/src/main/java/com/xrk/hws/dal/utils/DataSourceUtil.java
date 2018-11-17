package com.xrk.hws.dal.utils;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.xrk.hws.common.io.Resources;
import com.xrk.hws.common.logger.Logger;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.datasource.pooled.PooledDataSource;
import com.xrk.hws.dal.datasource.unpooled.UnpooledDataSource;

/**
 * 类: 数据库连接组件.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DataSourceUtil
{
	private static final String DEFAULT_DATASOURCE_FILE = "datasource.properties";
	
	/**
	 * 获取不带连接沲的数据源对象..
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static UnpooledDataSource getUnpooledDataSource(String resource) throws IOException
	{
		Properties props = Resources.getResourceAsProperties(resource);
		UnpooledDataSource dataSource = new UnpooledDataSource();
		
		dataSource.setDriver(props.getProperty("driver"));
		dataSource.setJdbcUrl(props.getProperty("url"));
		dataSource.setUsername(props.getProperty("username"));
		dataSource.setPassword(props.getProperty("password"));
		
		return dataSource;
	}
	
	/**
	 * 获取不带连接沲的数据源对象.
	 * 
	 * @param server
	 * @return
	 * @throws IOException
	 */
	public static UnpooledDataSource getUnpooledDataSource(DBServer server) throws IOException
	{
		UnpooledDataSource dataSource = new UnpooledDataSource();
		
		// 设置数据源连接配置
		dataSource.setDriver(server.getDriver());
		dataSource.setJdbcUrl(server.getUrl());
		dataSource.setUsername(server.getUser());
		dataSource.setPassword(server.getPass());
		return dataSource;
	}
	
	/**
	 * 获取带连接沲的数据源对象
	 * 
	 * @param resource	资源文件.
	 * @return
	 * @throws IOException
	 */
	public static PooledDataSource getPooledDataSource(String resource) throws IOException
	{
		Properties props = Resources.getResourceAsProperties(resource);
		PooledDataSource dataSource = new PooledDataSource();
		
		
		// 数据库侦听检测
		dataSource.setPoolPingEnabled(true);
		dataSource.setPoolPingQuery("select 1");
		
		// 设置数据源连接配置
		dataSource.setDriver(props.getProperty("driver"));
		dataSource.setJdbcUrl(props.getProperty("url"));
		dataSource.setUsername(props.getProperty("username"));
		dataSource.setPassword(props.getProperty("password"));
		
		return dataSource;
	}
	
	/**
	 * 获取带连接沲的数据源对象.
	 * 
	 * @param server	数据服务对象
	 * @return
	 * @throws IOException
	 */
	public static PooledDataSource getPooledDataSource(DBServer server) throws IOException
	{
		PooledDataSource dataSource = new PooledDataSource();
		
		// 数据库侦听检测
		dataSource.setPoolPingEnabled(true);
		dataSource.setPoolPingQuery("select 1");
		
		// 设置数据源连接配置
		dataSource.setDriver(server.getDriver());
		dataSource.setJdbcUrl(server.getUrl());
		dataSource.setUsername(server.getUser());
		dataSource.setPassword(server.getPass());
		
		return dataSource;
	}
	
	public static DataSource createBoneCpDataSource(DBServer server)
	{
		BoneCPDataSource  dataSource = new BoneCPDataSource();
		
		dataSource.setJdbcUrl(server.getUrl());
		dataSource.setUsername(server.getUser());
		dataSource.setPassword(server.getPass());
		dataSource.setDriverClass(server.getDriver());
		
		//设置空闲连接的存活时间。这个参数默认为60，单位:分钟
		dataSource.setIdleMaxAgeInMinutes(30);
		//设置connection的存活时间。这个参数默认为0，单位：毫秒。设置为0该功能失效
		dataSource.setMaxConnectionAgeInSeconds(60000);
		//设置测试connection的间隔时间。这个参数默认为240，单位：分钟。设置为0该功能失效。
		dataSource.setIdleConnectionTestPeriodInMinutes(30);
		//设置分区个数。这个参数默认为1，建议3-4（根据特定应用程序而定）
		dataSource.setPartitionCount(4);
		//设置每个分区含有connection最大个数。这个参数默认为2。如果小于2，BoneCP将设置为50。 
		dataSource.setMaxConnectionsPerPartition(100);
		//设置每个分区含有connection最大小个数。这个参数默认为0。
		dataSource.setMinConnectionsPerPartition(3);
		//设置分区中的connection增长数量。这个参数默认为1。
		dataSource.setAcquireIncrement(3);
		//设置获取connection超时的时间。单位：毫秒
		dataSource.setConnectionTimeoutInMs(2000);
		//设置重新获取连接的次数间隔时间。这个参数默认为7000，单位：毫秒。如果小于等于0，BoneCP将设置为1000
		dataSource.setAcquireRetryDelayInMs(7000);
		//设置连接检测语句
		dataSource.setConnectionTestStatement("SELECT 1");
		
		dataSource.setStatementsCacheSize(100);
		
		return dataSource;
	}
	
	public static DataSource createDruidDataSource(DBServer server)
	{
		DruidDataSource  dataSource = null;
		Properties props = null;
		try 
		{
			props = Resources.getResourceAsProperties(DEFAULT_DATASOURCE_FILE);
			dataSource = new DruidDataSource();
			dataSource.setUrl(server.getUrl());
			dataSource.setUsername(server.getUser());
			dataSource.setPassword(server.getPass());
			dataSource.setDriverClassName(server.getDriver());
			
			//初始化连接数量
			dataSource.setInitialSize(Integer.parseInt(props.getProperty("datasource.initSize","5")));
			//最大并发连接数
			dataSource.setMaxActive(Integer.parseInt(props.getProperty("datasource.maxActive","200")));
			//最小空闲连接数
			dataSource.setMinIdle(Integer.parseInt(props.getProperty("datasource.minIdle","3")));
			//配置获取连接等待超时的时间
			dataSource.setMaxWait(Integer.parseInt(props.getProperty("datasource.maxWait","6000")));
			//超过时间限制是否回收
			dataSource.setRemoveAbandoned(true);
			//超过时间限制多长
			dataSource.setRemoveAbandonedTimeout(Integer.parseInt(props.getProperty("datasource.removeAbandonedTimeout","180")));
			//配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
			dataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(props.getProperty("datasource.timeBetweenEvictionRunsMillis","60000")));
			//配置一个连接在池中最小生存的时间，单位是毫秒
			dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(props.getProperty("datasource.minEvictableIdleTimeMillis","300000")));
			//用来检测连接是否有效的sql，要求是一个查询语句
			dataSource.setValidationQuery("SELECT 1");
			//申请连接的时候检测 
			dataSource.setTestWhileIdle(true);
			//申请连接时执行validationQuery检测连接是否有效，配置为true会降低性能
			dataSource.setTestOnBorrow(false);
			//归还连接时执行validationQuery检测连接是否有效，配置为true会降低性能
			dataSource.setTestOnReturn(false);
			//打开PSCache，并且指定每个连接上PSCache的大小
			dataSource.setPoolPreparedStatements(true);
			//最大缓存语句
			dataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(props.getProperty("datasource.maxPoolPreparedStatementPerConnectionSize","200")));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			Logger.error("Not found datasource properties file:",DEFAULT_DATASOURCE_FILE);
		}
		return dataSource;
	}
}
