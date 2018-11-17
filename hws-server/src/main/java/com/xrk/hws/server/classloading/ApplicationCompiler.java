package com.xrk.hws.server.classloading;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import com.xrk.hws.server.Hws;

/**
 * 类: 应用编译器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ApplicationCompiler
{
	/**
	 * 包缓存，缓存所有编译过的包.
	 */
	Map<String, Boolean>	packagesCache	= new HashMap<String, Boolean>();
	/**
	 * 应用类.
	 */
	ApplicationClasses	 applicationClasses;
	/**
	 * 缓存和设置编译环境.
	 */
	Map<String, String>	 settings;
	
	/**
	 * 编译配置选项.
	 */
	public ApplicationCompiler(ApplicationClasses applicationClasses)
	{
		this.applicationClasses = applicationClasses;
		this.settings = new HashMap<String, String>();
		
		this.settings.put(CompilerOptions.OPTION_ReportMissingSerialVersion, CompilerOptions.IGNORE);
		this.settings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
		this.settings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
		this.settings.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
		this.settings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
		this.settings.put(CompilerOptions.OPTION_Encoding, "UTF-8");
		this.settings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
		String javaVersion = CompilerOptions.VERSION_1_6;
		if (System.getProperty("java.version").startsWith("1.5")) 
		{
			javaVersion = CompilerOptions.VERSION_1_5;
		}
		else if (System.getProperty("java.version").startsWith("1.6")) 
		{
			javaVersion = CompilerOptions.VERSION_1_6;
		}
		else if (System.getProperty("java.version").startsWith("1.7")) 
		{
			javaVersion = CompilerOptions.VERSION_1_7;
		}
		if ("1.5".equals(Hws.configuration.get("java.source"))) 
		{
			javaVersion = CompilerOptions.VERSION_1_5;
		}
		else if ("1.6".equals(Hws.configuration.get("java.source")))
		{
			javaVersion = CompilerOptions.VERSION_1_6;
		}
		else if ("1.7".equals(Hws.configuration.get("java.source"))) 
		{
			javaVersion = CompilerOptions.VERSION_1_7;
		}

		// 添加编译选项到缓存中
		this.settings.put(CompilerOptions.OPTION_Source, javaVersion);
		this.settings.put(CompilerOptions.OPTION_TargetPlatform, javaVersion);
		this.settings.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.PRESERVE);
		this.settings.put(CompilerOptions.OPTION_Compliance, javaVersion);
	}
}
