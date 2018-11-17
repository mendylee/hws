package com.xrk.hws.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.xrk.hws.server.classloading.ApplicationClasses;
import com.xrk.hws.server.classloading.ApplicationClassloader;
import com.xrk.hws.server.plugins.PluginCollection;
import com.xrk.hws.server.vfs.VirtualFile;

/**
 * Hws: Hws应用框架类.
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
public class Hws
{
	/**
	 * 运行模式,目前支持两种运行模式(DEV,PROD)
	 */
	public enum Mode
	{
		DEV, 
		PROD;

		public boolean isDev()
		{
			return this == DEV;
		}

		public boolean isProd()
		{
			return this == PROD;
		}
	}
	
	/**
	 * 应用程序是否初始化
	 */
	public static boolean	               initialized	       = false;

	/**
	 * 应用程序是否已经启动
	 */
	public static boolean	               isStarted	           = false;

	/**
	 * 是否发送kill -9的信号?
	 */
	public static boolean	               killsig	           = false;

	/**
	 * 系统关闭钩子是否可用?
	 */
    private static boolean	               shutdownHookEnabled	= false;
	
	/**
	 * 框架ID
	 */
	public static String	               id;

	/**
	 * 应用程序运行时间模式.
	 */
	public static Mode	                   mode;

	/**
	 * 应用程序根路径.
	 */
	public static File	                   applicationPath	   = null;

	/**
	 * 临时目录.
	 */
	public static File	                   tmpDir	           = null;

	/**
	 * 是否是只读临时目录.
	 */
	public static boolean	               readOnlyTmp	       = false;

	/**
	 * 框架根路径.
	 */
	public static File	                   frameworkPath	   = null;
	
	/**
	 * 已加载的应用程序类.
	 */
	public static ApplicationClasses	   classes;

	/**
	 * 应用程序类加载器.
	 */
	public static ApplicationClassloader	classloader;
	
	/**
	 * 文件路径列表.
	 */
	public static List<VirtualFile>	       roots	           = new ArrayList<VirtualFile>(16);

	/**
	 * Java文件路径.
	 */
	public static List<VirtualFile>	       javaPath;

	/**
	 * 模版文件路径列表.
	 */
	public static List<VirtualFile>	       templatesPath;

	/**
	 * 主路由配置文件.
	 */
	public static VirtualFile	           routes;

	/**
	 * 插件路由
	 */
	public static Map<String, VirtualFile>	modulesRoutes;

	/**
	 * 应用主配置文件：application.conf
	 */
	public static VirtualFile	           conf;

	/**
	 * 应用配置(根据框架ID解析过的环境配置)
	 */
	public static Properties	           configuration;

	/**
	 * 应用最后一次启动时间.
	 */
	public static long	                   startedAt;

	/**
	 * 国际化支持列表.
	 */
	public static List<String>	           langs	           = new ArrayList<String>(16);

	/**
	 * 安全加密串key.
	 */
	public static String	               secretKey;

	/**
	 * 插件集合，加载所有可用的插件.
	 */
	public static PluginCollection	       pluginCollection	   = new PluginCollection();

	/**
	 * 应用模块.
	 */
	public static Map<String, VirtualFile>	modules	           = new HashMap<String, VirtualFile>(16);

	/**
	 * 框架版本号.
	 */
	public static String	               version	           = null;

	/**
	 * 上下文路径(当多个应用程序布署在同一台机器时)
	 */
	public static String	               ctxPath	           = "";
	static boolean	                       firstStart	       = true;
	public static boolean	               usePrecompiled	   = false;
	public static boolean	               forceProd	       = false;
	
	/**
	 * 根据路径获取虚拟文件.  
	 *    
	 * @param path		文件路径.
	 * @return
	 */
	public static VirtualFile getVirtualFile(String path)
	{
		return VirtualFile.search(roots, path);
	}

	/**
	 * 根据指定的路径获取文件.  
	 *    
	 * @param path		文件路径.
	 * @return
	 */
	public static File getFile(String path)
	{
		return new File(applicationPath, path);
	}

	/**
	 * 是否运行测试模式.  
	 *    
	 * @return
	 */
	public static boolean runingInTestMode()
	{
		return id.matches("test|test-?.*");
	}

}
