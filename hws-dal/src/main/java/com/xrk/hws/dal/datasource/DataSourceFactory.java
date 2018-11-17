package com.xrk.hws.dal.datasource;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * 接口: 抽象数据源工厂接口.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface DataSourceFactory
{
	/**
	 * 设置属性值.  
	 */
	void setProperties(Properties properties);
	
	/**
	 * 获取数据源对象.  
	 *    
	 * @return	数据源对象实例.
	 */
	DataSource getDataSource();
}
