package com.xrk.hws.common.reflection.invoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 类: 设置属性值.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SetFieldInvoker implements Invoker
{
	private Field field;

	public SetFieldInvoker(Field field) 
	{
		this.field = field;
	}

	@Override
	public Object invoke(Object target, Object[] args) throws IllegalAccessException,InvocationTargetException
	{
		field.set(target, args[0]);
		return null;
	}

	@Override
	public Class<?> getType()
	{
		return field.getType();
	}
}
