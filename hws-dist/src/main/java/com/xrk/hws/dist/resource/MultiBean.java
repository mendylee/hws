package com.xrk.hws.dist.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.xrk.hws.dist.core.ResourceBean;

/**
 * 类: 国际化多语言支持.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MultiBean extends ResourceBean
{
	/**
	 * 本地语言代码.
	 */
	private String nativeLangCode;
	
	public MultiBean(String langCode)
	{
		super();
		resourcesName = "META-INF/config";
		init(langCode);
	}
	
	public void init(String langCode)
	{
		if(langCode==null)
		{
			bundle = ResourceBundle.getBundle(resourcesName, Locale.getDefault());
		}
		else if(langCode.toUpperCase().equals("ISO-8859-1"))
		{
			nativeLangCode = "ISO-8859-1";
			bundle = ResourceBundle.getBundle(resourcesName, Locale.US);
		}
		else if(langCode.toUpperCase().equals("GB2312"))
		{
			nativeLangCode = "GB2312";
			bundle = ResourceBundle.getBundle(resourcesName, Locale.PRC);
		}
		else if(langCode.toUpperCase().equals("BIG5"))
		{
			nativeLangCode = "BIG5";
			bundle = ResourceBundle.getBundle(resourcesName, Locale.TAIWAN);//new Locale("zh", "TW");
		}
	}
	
	public String getString(String keyWord)
	{		
		return getString(keyWord, "");
	}
	
	public String getString(String keyWord, String topStr)
	{		
		String str = "";
		try
		{
			str = bundle.getString(keyWord);
		}
		catch(MissingResourceException ex)
		{
			str = topStr+keyWord;
		}		
		return str;		
	}
	
	public String getSpace()
	{
		String space = "";
		if(nativeLangCode!=null&&nativeLangCode.equals("ISO-8859-1"))
			space = "&nbsp;";
	
		return space;
	}
	
	public String getFileString(String relativeUri)
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			Reader f = new InputStreamReader(this.getClass().getResourceAsStream(relativeUri));
			BufferedReader fb = new BufferedReader(f);
			String s = "";
			while((s=fb.readLine())!=null)
			{
				sb = sb.append(s);
			}
			f.close();
			fb.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return sb.toString();
	}
}
