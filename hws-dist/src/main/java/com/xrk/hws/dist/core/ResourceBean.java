package com.xrk.hws.dist.core;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 * 类: 资源.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ResourceBean 
{
	/**
	 * 资源名称.
	 */
	protected String resourcesName;
	/**
	 * (国际化)资源绑定.
	 */
	protected ResourceBundle bundle;
	
	public ResourceBean(){}
	
	public ResourceBean(String resourcesName)
	{
		bundle = ResourceBundle.getBundle(resourcesName);
	}
	
	public String getString(String keyWord)
	{		
		String str = "";
		try
		{
			str = bundle.getString(keyWord);
		}
		catch(MissingResourceException ex)
		{
			System.err.println(ex);
		}		
		return str;		
	}
	
	public static void main(String[] args)
	{
		ResourceBean rb = new ResourceBean("config");
		System.out.println(rb.getString("QSXYSJ"));
		
		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
        System.out.println(ResourceBean.class.getClassLoader().getResource(""));
        System.out.println(ClassLoader.getSystemResource(""));
        System.out.println(ResourceBean.class.getResource(""));
        System.out.println(ResourceBean.class.getResource("/"));
        System.out.println(new java.io.File("").getAbsolutePath());
        System.out.println(System.getProperty("user.dir"));
	}
}
