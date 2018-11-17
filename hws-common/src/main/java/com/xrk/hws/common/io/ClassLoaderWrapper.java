package com.xrk.hws.common.io;

import java.io.InputStream;
import java.net.URL;

/**
 * 类: 类加载器包装器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ClassLoaderWrapper
{
	/**
	 * 默认类加载器.
	 */
	ClassLoader defaultClassLoader;
	/**
	 * 系统类加载器.
	 */
	ClassLoader systemClassLoader;
	
	ClassLoaderWrapper() 
	{
		try 
		{
			systemClassLoader = ClassLoader.getSystemClassLoader();
		}
		catch (SecurityException ignored) 
		{
			
		}
	}
	
	public URL getResourceAsURL(String resource)
	{
		return getResourceAsURL(resource, getClassLoaders(null));
	}
	
	public URL getResourceAsURL(String resource, ClassLoader classLoader)
	{
		return getResourceAsURL(resource, getClassLoaders(classLoader));
	}
	
	public InputStream getResourceAsStream(String resource)
	{
		return getResourceAsStream(resource, getClassLoaders(null));
	}

	public InputStream getResourceAsStream(String resource, ClassLoader classLoader)
	{
		return getResourceAsStream(resource, getClassLoaders(classLoader));
	}

	public Class<?> classForName(String name) throws ClassNotFoundException
	{
		return classForName(name, getClassLoaders(null));
	}
	
	public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException
	{
		return classForName(name, getClassLoaders(classLoader));
	}
	
	InputStream getResourceAsStream(String resource, ClassLoader[] classLoader)
	{
		for (ClassLoader cl : classLoader) 
		{
			if (null != cl) 
			{
				InputStream returnValue = cl.getResourceAsStream(resource);
				if (null == returnValue) 
				{
					returnValue = cl.getResourceAsStream("/" + resource);
				}
				if (null != returnValue) 
				{
					return returnValue;
				}
			}
		}
		return null;
	}
	
	URL getResourceAsURL(String resource, ClassLoader[] classLoader) 
	{
	    URL url;
	    for (ClassLoader cl : classLoader) 
	    {
	      if (null != cl) 
	      {
	        url = cl.getResource(resource);
	        if (null == url) 
	        {
	          url = cl.getResource("/" + resource);
	        }
	        if (null != url) 
	        {
	          return url;
	        }
	      }
	    }
	    return null;
	  }
	
	Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException
	{
		for (ClassLoader cl : classLoader) 
		{
			if (null != cl) 
			{

				try 
				{
					Class<?> c = Class.forName(name, true, cl);
					if (null != c) 
					{
						return c;
					}
				}
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}
		throw new ClassNotFoundException("Cannot find class: " + name);
	}
	
	ClassLoader[] getClassLoaders(ClassLoader classLoader)
	{
		return new ClassLoader[] { classLoader, 
		                           defaultClassLoader,
		                           Thread.currentThread().getContextClassLoader(),
		                          getClass().getClassLoader(), systemClassLoader };
	}
}
