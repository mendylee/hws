package com.xrk.hws.dal.core;

import java.util.HashMap;

/**
 * 类: 数据基本操作命令类型. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class CommandType
{
	//数据基本操作类型常量定义，操作助记符
	public final static  int FINDONE   = 1;
	public final static  int MFIND     = 2;
	public final static  int INSETONE  = 3;
	public final static  int MINSET    = 4;
	public final static  int UPDATE    = 5;
	public final static  int DELETE    = 6;
	public final static  int COUNT     = 7;
	public final static  int AGGRET	   = 8;
	public final static  int SEQUENCE  = 9;
	
	/**
	 * 操作助记符字典表
	 */
	public static  HashMap<String,Integer> typeMap= new HashMap<String,Integer>();
    
	static
	{
		typeMap.put("findone", FINDONE);
		typeMap.put("mfind", MFIND);
		typeMap.put("insertone", INSETONE);
		typeMap.put("minsert", MINSET);
		typeMap.put("update", UPDATE);
		typeMap.put("delete", DELETE);
		typeMap.put("count", COUNT);
		typeMap.put("aggregate", AGGRET);
		typeMap.put("sequence", SEQUENCE);
	}

	/**
	 * 
	 * 根据指定的操作名称，获取指定名称所对应的助记符常量  
	 *    
	 * @param name	操作名称
	 * @return		返回操作名称所对应的常量
	 */
	public static  int nameToType(String name)
	{
		Integer type = typeMap.get(name);
		if (type != null)
		{
			return type;
		}

		return -1;
	}
	
	/**
	 * 
	 * 获取操作类型名称.  
	 *    
	 * @param type
	 * @return
	 */
	public static String typeToName(int type)
	{
		String name = null;
		switch (type) 
		{
			case FINDONE:
				name = "findone";
				break;
			case MFIND :
				name = "mfind";
				break;
			case INSETONE:
				name = "insertone";
				break;
			case MINSET:
				name = "minsert";
				break;
			case UPDATE:
				name = "update";
				break;
			case DELETE:
				name = "delete";
				break;
			case COUNT:
				name = "count";
				break;
			case AGGRET:
				name = "aggregate";
				break;
			case SEQUENCE:
				name = "sequence";
			default:
				name = "not support";
				break;
		}
		return name;
	}
}
