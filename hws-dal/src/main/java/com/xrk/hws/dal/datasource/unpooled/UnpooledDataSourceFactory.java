package com.xrk.hws.dal.datasource.unpooled;

import java.util.Properties;

import javax.sql.DataSource;

import com.xrk.hws.common.reflection.MetaObject;
import com.xrk.hws.common.reflection.SystemMetaObject;
import com.xrk.hws.dal.datasource.DataSourceFactory;
import com.xrk.hws.dal.exception.DataSourceException;

/**
 * 类: 非连接沲数据源工厂.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UnpooledDataSourceFactory implements DataSourceFactory
{
	private static final String DRIVER_PROPERTY_PREFIX = "driver.";
	private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();

	protected DataSource dataSource;

	public UnpooledDataSourceFactory() 
	{
		this.dataSource = new UnpooledDataSource();
	}

	@Override
	public void setProperties(Properties properties)
	{
		Properties driverProperties = new Properties();
	    MetaObject metaDataSource = SystemMetaObject.forObject(dataSource);
	    for (Object key : properties.keySet()) {
	      String propertyName = (String) key;
	      if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) {
	        String value = properties.getProperty(propertyName);
	        driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
	      } else if (metaDataSource.hasSetter(propertyName)) {
	        String value = (String) properties.get(propertyName);
	        Object convertedValue = convertValue(metaDataSource, propertyName, value);
	        metaDataSource.setValue(propertyName, convertedValue);
	      } else {
	        throw new DataSourceException("Unknown DataSource property: " + propertyName);
	      }
	    }
	    if (driverProperties.size() > 0) {
	      metaDataSource.setValue("driverProperties", driverProperties);
	    }
	}

	@Override
	public DataSource getDataSource()
	{
		return dataSource;
	}
	
	/**
	 * 值转换.  
	 *    
	 * @param metaDataSource	数据源元数据对象.
	 * @param propertyName		属性名称.
	 * @param value				属性值.
	 * @return					转换后的值对象.
	 */
	private Object convertValue(MetaObject metaDataSource, String propertyName, String value)
	{
		Object convertedValue = value;
		Class<?> targetType = metaDataSource.getSetterType(propertyName);
		if (targetType == Integer.class || targetType == int.class) {
			convertedValue = Integer.valueOf(value);
		}
		else if (targetType == Long.class || targetType == long.class) {
			convertedValue = Long.valueOf(value);
		}
		else if (targetType == Boolean.class || targetType == boolean.class) {
			convertedValue = Boolean.valueOf(value);
		}
		return convertedValue;
	}

}
