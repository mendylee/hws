package com.xrk.hws.common.reflection.property;

import java.util.Locale;

import com.xrk.hws.common.reflection.ReflectionException;

/**
 * 类: 属性命名.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public final class PropertyNamer
{
	private PropertyNamer() {}

	public static String methodToProperty(String name)
	{
		if (name.startsWith("is")) {
			name = name.substring(2);
		}
		else if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		}
		else {
			throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
		}

		if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
			name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
		}

		return name;
	}

	public static boolean isProperty(String name)
	{
		return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
	}

	public static boolean isGetter(String name)
	{
		return name.startsWith("get") || name.startsWith("is");
	}

	public static boolean isSetter(String name)
	{
		return name.startsWith("set");
	}

}
