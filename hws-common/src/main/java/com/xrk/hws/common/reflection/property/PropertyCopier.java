package com.xrk.hws.common.reflection.property;

import java.lang.reflect.Field;

/**
 * PropertyCopier: PropertyCopier.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public final class PropertyCopier
{
	private PropertyCopier() {}

	/**
	 * 拷贝对象属性.  
	 *    
	 * @param type				类型.
	 * @param sourceBean		源对象.
	 * @param destinationBean	目标对象.
	 */
	public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean)
	{
		Class<?> parent = type;
		while (parent != null) 
		{
			final Field[] fields = parent.getDeclaredFields();
			for (Field field : fields) 
			{
				try 
				{
					field.setAccessible(true);
					field.set(destinationBean, field.get(sourceBean));
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			parent = parent.getSuperclass();
		}
	}
}
