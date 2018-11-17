package com.xrk.hws.server.classloading;

import java.security.ProtectionDomain;

import com.xrk.hws.server.classloading.hash.ClassStateHashCreator;

/**
 * 类: 应用类加载器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@SuppressWarnings("unused")
public class ApplicationClassloader extends ClassLoader
{
	/**
	 * 类状态hash的创造器.
	 */
    private final ClassStateHashCreator	classStateHashCreator	= new ClassStateHashCreator();

	/**
	 * 当前应用类加载状态.
	 */
	public ApplicationClassloaderState	currentState	      = new ApplicationClassloaderState();

	/**
	 * 受保护域用于所有加载的类.
	 */
	public ProtectionDomain	            protectionDomain;

	public ApplicationClassloader()
	{
		
	}
}
