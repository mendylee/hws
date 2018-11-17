package com.xrk.hws.dal.datasource.jndi;

import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.xrk.hws.dal.datasource.DataSourceFactory;
import com.xrk.hws.dal.exception.DataSourceException;

/**
 * 类: JNDI(Java命名系统接口)数据源工厂.
 * 
 * 1.JNDI(Java Naming and Directory Interface,Java命名和目录接口)是SUN公司提供的一种标准的Java命名系统接口</br>
 * JNDI提供统一的客户端API，通过不同的访问提供者接口JNDI服务供应接口(SPI)的实现</br>
 * 由管理者将JNDI API映射为特定的命名服务和目录系统，使得Java应用程序可以和这些命名服务和目录服务之间进行交互。</br>
 * 目录服务是命名服务的一种自然扩展。两者之间的关键差别是目录服务中对象不但可以有名称还可以有属性（例如，用户有email地址）</br>
 * 而命名服务中对象没有属性</br>
 * 2.更多资料请参考:http://baike.baidu.com/link?url=x1q5-Rlq-5BVx9Lp0Rr6uUYsSaMKfVeSfdwV81do88E7F6iE3nvlGHJM3GCvw3TUrbU5QzhOjFtm2AdGRbq_Ga
 * 
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class JndiDataSourceFactory implements DataSourceFactory
{
	public static final String INITIAL_CONTEXT = "initial_context";
	public static final String DATA_SOURCE = "data_source";
	public static final String ENV_PREFIX = "env.";
	
	private DataSource dataSource;
	
	@Override
    public void setProperties(Properties properties)
    {
		try {
			InitialContext initCtx = null;
			Properties env = getEnvProperties(properties);
			if (env == null) {
				initCtx = new InitialContext();
			}
			else {
				initCtx = new InitialContext(env);
			}

			if (properties.containsKey(INITIAL_CONTEXT) && properties.containsKey(DATA_SOURCE)) {
				Context ctx = (Context) initCtx.lookup(properties.getProperty(INITIAL_CONTEXT));
				dataSource = (DataSource) ctx.lookup(properties.getProperty(DATA_SOURCE));
			}
			else if (properties.containsKey(DATA_SOURCE)) {
				dataSource = (DataSource) initCtx.lookup(properties.getProperty(DATA_SOURCE));
			}
		}
		catch (NamingException e) 
		{
			throw new DataSourceException("There was an error configuring JndiDataSourceTransactionPool. Cause: "+ e, e);
		}
    }
	
	@Override
	public DataSource getDataSource()
	{
		return dataSource;
	}

    private static Properties getEnvProperties(Properties allProps)
    {
		final String PREFIX = ENV_PREFIX;
	    Properties contextProperties = null;
	    for (Entry<Object, Object> entry : allProps.entrySet()) 
	    {
	      String key = (String) entry.getKey();
	      String value = (String) entry.getValue();
	      if (key.startsWith(PREFIX)) 
	      {
	        if (contextProperties == null) 
	        {
	          contextProperties = new Properties();
	        }
	        contextProperties.put(key.substring(PREFIX.length()), value);
	      }
	    }
	    return contextProperties;
    }

}
