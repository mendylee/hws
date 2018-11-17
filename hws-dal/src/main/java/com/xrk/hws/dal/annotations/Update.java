package com.xrk.hws.dal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解: 数据更新操作注解标识.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update 
{
	  String[] value();
}
