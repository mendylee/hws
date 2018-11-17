package com.xrk.hws.server.classloading;

import java.io.File;
import java.io.FileOutputStream;

import com.xrk.hws.server.Hws;
import com.xrk.hws.server.vfs.VirtualFile;

/**
 * 类: 应用程序类.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ApplicationClasses
{

	/**
	 * 表示一个应用程序类.
	 */
	public static class ApplicationClass
	{

		/**
		 * 类的全名.
		 */
		public String		name;
		/**
		 * 引用的java源文件.
		 */
		public VirtualFile	javaFile;
		/**
		 * java源文件.
		 */
		public String		javaSource;
		/**
		 * 编译后的字节码.
		 */
		public byte[]		javaByteCode;
		/**
		 * 增强字节码.
		 */
		public byte[]		enhancedByteCode;
		/**
		 * JVM加载的类.
		 */
		public Class<?>		javaClass;
		/**
		 * JVM加载的包.
		 */
		public Package		javaPackage;
		/**
		 * 最后一次编译的时间.
		 */
		public Long		   timestamp	= 0L;
		/**
		 * 该类是否已编译?
		 */
		boolean		       compiled;
		/**
		 * 签名校验和，用于判断是否相同
		 */
		public int		   sigChecksum;

		public ApplicationClass()
		{
		}

		/**
		 * 需要刷新这个类 !
		 */
		public void refresh()
		{
			if (this.javaFile != null) {
				this.javaSource = this.javaFile.contentAsString();
			}
			this.javaByteCode = null;
			this.enhancedByteCode = null;
			this.compiled = false;
			this.timestamp = 0L;
		}

		/**
		 * 增强这个类
		 * 
		 * @return 增强后的字节码.
		 * @throws Exception 
		 */
		public byte[] enhance() throws Exception
		{
			this.enhancedByteCode = this.javaByteCode;
			if (isClass()) 
			{
				Hws.pluginCollection.enhance(this);
			}
			if (System.getProperty("precompile") != null) 
			{
				try 
				{
					File f = Hws.getFile("precompiled/java/" + (name.replace(".", "/")) + ".class");
					f.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(this.enhancedByteCode);
					fos.close();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			return this.enhancedByteCode;

		}

		/**
		 * 是否已编译，但却未定义?
		 * 
		 * @return if the class is compiled but not defined
		 */
		public boolean isDefinable()
		{
			return compiled && javaClass != null;
		}

		/**
		 * 是否是一个类?
		 *   
		 * @return    
		 */
		public boolean isClass()
		{
			return !name.endsWith("package-info");
		}

		/**
		 * 获取包名称.
		 *   
		 * @return    
		 */
		public String getPackage()
		{
			int dot = name.lastIndexOf('.');
			return dot > -1 ? name.substring(0, dot) : "";
		}

		/**
		 * 卸载类.
		 */
		public void uncompile()
		{
			this.javaClass = null;
		}

		/**
		 * 类编译回调.
		 * 
		 * @param code	字节码.
		 */
		public void compiled(byte[] code)
		{
			javaByteCode = code;
			enhancedByteCode = code;
			compiled = true;
			this.timestamp = this.javaFile.lastModified();
		}

		@Override
		public String toString()
		{
			return name + " (compiled:" + compiled + ")";
		}
	}
}
