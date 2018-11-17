package com.xrk.hws.dal.datasource.pooled;

import com.xrk.hws.dal.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * 类: 带连接沲的数据源工厂类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory
{
	public PooledDataSourceFactory() 
	{
		this.dataSource = new PooledDataSource();
	}
}
