package com.xrk.hws.dist.config;

import com.xrk.hws.dist.core.ObjValue;
import com.xrk.hws.dist.resource.MultiBean;

/**
 * 类: 配置文件上下文.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ConfigContext
{
	private static MultiBean mb = null;
	private static String QSXYSJ=null,YMMZ=null,RZDY=null,YCDYXY=null,DMY=null,AQCL=null,POLICY=null,LSML=null,SERVICEONWORKER=null;
	private static long TMOT=-1;
	
	static String configFile = "config.xml";
	private static ObjValue USERS = null;
	
	public static int getInitServices()
    {
	    return 0;
    }

	public static int getMaxServices()
    {
	    return 0;
    }
}
