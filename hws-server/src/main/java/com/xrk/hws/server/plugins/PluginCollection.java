package com.xrk.hws.server.plugins;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.xrk.hws.server.Hws;
import com.xrk.hws.server.classloading.ApplicationClasses.ApplicationClass;

/**
 * 类: 插件集合.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PluginCollection
{
	/**
	 * 插件优先级 (0 为最高优先级加载)
	 */
	public int	index;

	/**
	 * 加载插件
	 */
	public void onLoad()
	{
	}

	/**
	 * 是否编译源文件
	 *   
	 * @return    
	 */
	public boolean compileSources()
	{
		return false;
	}
	
	
	/**
	 * 绑定java对象到HTTP请求参数
	 * 
	 * @param <T>
	 */
	public <T> Object bind(String name, Class<T> clazz, Type type, Annotation[] annotations, Map<String, String[]> params)
	{
		return null;
	}

	public <T> Object bind(String name, T o, Map<String, String[]> params)
	{
		return null;
	}

	public <T> Map<String, T> unBind(T src, String name)
	{
		return null;
	}

	public String getMessage(String locale, Object key, Object... args)
	{
		return null;
	}
	
	/**
	 * 获取插件状态.
	 */
	public String getStatus()
	{
		return null;
	}

	/**
	 * 返回插件的json串.
	 */
	public JsonObject getJsonStatus()
	{
		return null;
	}

	/**
	 * 增强这个类
	 * 
	 * @param applicationClass
	 * @throws java.lang.Exception
	 */
	public void enhance(ApplicationClass applicationClass) throws Exception
	{
		
	}

	
	/**
	 * 插件发生改变前.
	 */
	public void beforeDetectingChanges()
	{
		
	}

	/**
	 * 插件发生改变.
	 */
	public void detectChange()
	{
		
	}
	
	/**
	 * 发现类发生改变
	 * @return    
	 */
	public boolean detectClassesChange()
	{
		return false;
	}

	/**
	 * 应用启动
	 */
	public void onApplicationStart()
	{
		
	}

	/**
	 * 应用启动后.
	 */
	public void afterApplicationStart()
	{
	}

	/**
	 * 应用停止时调用.
	 */
	public void onApplicationStop()
	{
		
	}

	/**
	 * 调用前的准备.
	 */
	public void beforeInvocation()
	{
		
	}

	/**
	 * 收尾
	 */
	public void afterInvocation()
	{
	}

	/**
	 * 调用发生异常.
	 *   
	 * @param e    
	 */
	public void onInvocationException(Throwable e)
	{
		
	}

	public void invocationFinally()
	{
		
	}

	public void beforeActionInvocation(Method actionMethod)
	{
		
	}
	
	public void onInvocationSuccess()
	{
		
	}


	public void afterActionInvocation()
	{
		
	}

	public void onConfigurationRead()
	{
		
	}

	public void onRoutesLoaded()
	{
		
	}

	public void onEvent(String message, Object context)
	{
		
	}

	public List<String> addTemplateExtensions()
	{
		return new ArrayList<String>();
	}

	public Map<String, String> addMimeTypes()
	{
		return new HashMap<String, String>();
	}

	public void afterFixtureLoad()
	{
		
	}

	/**
	 * 插件内部通知事件.
	 */
	public static void postEvent(String message, Object context)
	{
		Hws.pluginCollection.onEvent(message, context);
	}

	public List<HwsPlugin> getEnabledPlugins()
    {
	    return null;
    }
}
