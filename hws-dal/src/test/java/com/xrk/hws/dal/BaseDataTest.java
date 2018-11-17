package com.xrk.hws.dal;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.xrk.hws.common.io.Resources;
import com.xrk.hws.dal.datasource.pooled.PooledDataSource;
import com.xrk.hws.dal.datasource.unpooled.UnpooledDataSource;
import com.xrk.hws.dal.jdbc.ScriptRunner;

/**
 * 类: 基础数据测试类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月5日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class BaseDataTest
{
	public static final String BLOG_PROPERTIES = "com/xrk/hws/dal/databases/blog/blog-derby.properties";
	public static final String BLOG_DDL = "com/xrk/hws/dal/databases/blog/blog-derby-schema.sql";
	public static final String BLOG_DATA = "com/xrk/hws/dal/databases/blog/blog-derby-dataload.sql";

	public static final String JPETSTORE_PROPERTIES = "com/xrk/hws/dal/databases/jpetstore/jpetstore-hsqldb.properties";
	public static final String JPETSTORE_DDL = "com/xrk/hws/dal/databases/jpetstore/jpetstore-hsqldb-schema.sql";
	public static final String JPETSTORE_DATA = "com/xrk/hws/dal/databases/jpetstore/jpetstore-hsqldb-dataload.sql";
	
	/**
	 * 创建UnpooledDataSource对象.  
	 *    
	 * @param resource		资源文件.
	 * @return
	 * @throws IOException
	 */
	public static UnpooledDataSource createUnpooledDataSource(String resource) throws IOException
	{
		Properties props = Resources.getResourceAsProperties(resource);
		UnpooledDataSource ds = new UnpooledDataSource();
		
		ds.setDriver(props.getProperty("driver"));
		ds.setJdbcUrl(props.getProperty("url"));
		ds.setUsername(props.getProperty("username"));
		ds.setPassword(props.getProperty("password"));
		
		return ds;
	}
	
	/**
	 * 创建使用连接沲的数据源对象.  
	 *    
	 * @param resource		资源文件.
	 * @return
	 * @throws IOException
	 */
	public static PooledDataSource createPooledDataSource(String resource) throws IOException 
	{
	    Properties props = Resources.getResourceAsProperties(resource);
	    PooledDataSource ds = new PooledDataSource();
	    
	    ds.setDriver(props.getProperty("driver"));
	    ds.setJdbcUrl(props.getProperty("url"));
	    ds.setUsername(props.getProperty("username"));
	    ds.setPassword(props.getProperty("password"));
	    
	    return ds;
	  }
	
	public static void runScript(DataSource ds,String resource) throws IOException,SQLException
	{
		Connection connection = ds.getConnection();
		try
		{
			ScriptRunner runner = new ScriptRunner(connection);
			
			runner.setAutoCommit(true);
			runner.setStopOnError(false);
			runner.setLogWriter(null);
			runner.setErrorLogWriter(null);
			runScript(runner,resource);
		}
		finally
		{
			connection.close();
		}
	}
	
	public static void runScript(ScriptRunner runner, String resource) throws IOException,SQLException
	{
		Reader reader = Resources.getResourceAsReader(resource);
		try {
			runner.runScript(reader);
		}
		finally {
			reader.close();
		}
	}
	
	public static DataSource createBlogDataSource() throws IOException, SQLException
	{
		DataSource ds = createUnpooledDataSource(BLOG_PROPERTIES);
		runScript(ds, BLOG_DDL);
		runScript(ds, BLOG_DATA);
		return ds;
	}

	public static DataSource createJPetstoreDataSource() throws IOException, SQLException
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		runScript(ds, JPETSTORE_DDL);
		runScript(ds, JPETSTORE_DATA);
		return ds;
	}
}
