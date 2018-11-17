package com.xrk.hws.dal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xrk.hws.dal.common.DbType;

/**
 * 注解: 注解：数据库存储字段.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@Target(value={ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
	/** name, 字段名 */
	public String name();
	
	/** type, 字段类型*/
	public DbType type();

	/** 是否自增 */
	boolean isAutoIncre() default false;

	/** 是否存储null值 */
	boolean storeNull() default true;
}
