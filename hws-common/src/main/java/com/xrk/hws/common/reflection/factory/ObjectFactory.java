package com.xrk.hws.common.reflection.factory;

import java.util.List;
import java.util.Properties;

/**
 * 接口: 对象工厂.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface ObjectFactory
{
	void setProperties(Properties properties);

	<T> T create(Class<T> type);

	<T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

	<T> boolean isCollection(Class<T> type);
}
