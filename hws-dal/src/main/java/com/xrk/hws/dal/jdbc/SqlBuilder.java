package com.xrk.hws.dal.jdbc;

/**
 * 类: Sql语句构造器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月4日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SqlBuilder
{
	/**
	 * 用一个本地线程变量保存当前执行的SQL语句,以保证在多线程环境下是线程安全的.
	 */
	private static final ThreadLocal<SQL> localSQL = new ThreadLocal<SQL>();
	
	static
	{
		BEGIN();
	}
	
	private SqlBuilder() 
	{
	}
	
	/** 
	 * 开始准备构建新的sql语句.
	 */
	public static void BEGIN()
	{
		RESET();
	}

	/**
	 * 重新设置新的SQL语句.  
	 */
	public static void RESET()
	{
		localSQL.set(new SQL());
	}

	/**
	 * 构造更新数据SQL语句.  
	 *    
	 * @param table
	 */
	public static void UPDATE(String table)
	{
		sql().UPDATE(table);
	}

	/**
	 * 设置更新数据值.  
	 *    
	 * @param sets
	 */
	public static void SET(String sets)
	{
		sql().SET(sets);
	}
	
	/**
	 * 获取完整执行的SQL语句.  
	 *    
	 * @param sets
	 */
	public static String SQL()
	{
		try 
		{
			return sql().toString();
		}
		finally 
		{
			RESET();
		}
	}
	
	/**
	 * 构造INSERT操作语句.  
	 *    
	 * @param tableName		表名称.
	 */
	public static void INSERT_INTO(String tableName)
	{
		sql().INSERT_INTO(tableName);
	}

	/**
	 * 构造插入数据INSERT操作语句.  
	 *    
	 * @param column		列名称集合.
	 * @parma values		值集合.
	 */
	public static void VALUES(String columns, String values)
	{
		sql().VALUES(columns, values);
	}
	
	/**
	 * 构造检索列名SQL语句.  
	 *    
	 * @param columns		列名称集合.
	 */
	public static void SELECT(String columns)
	{
		sql().SELECT(columns);
	}

	/**
	 * 构造SELECT_DISTINCT列名SQL语句.  
	 *    
	 * @param columns		列名称集合.
	 */
	public static void SELECT_DISTINCT(String columns)
	{
		sql().SELECT_DISTINCT(columns);
	}
	
	/**
	 * 构造DELETE删除SQL语句.  
	 *    
	 * @param table
	 */
	public static void DELETE_FROM(String table)
	{
		sql().DELETE_FROM(table);
	}

	/**
	 * 构造FROM表名SQL语句.  
	 *    
	 * @param table
	 */
	public static void FROM(String table)
	{
		sql().FROM(table);
	}
	
	/**
	 * 构造join连接SQL语句.  
	 *    
	 * @param join
	 */
	public static void JOIN(String join)
	{
		sql().JOIN(join);
	}

	/**
	 * 构造内联SQL语句.  
	 *    
	 * @param join
	 */
	public static void INNER_JOIN(String join)
	{
		sql().INNER_JOIN(join);
	}
	
	/**
	 * 构造左外联SQL语句.  
	 *    
	 * @param join
	 */
	public static void LEFT_OUTER_JOIN(String join)
	{
		sql().LEFT_OUTER_JOIN(join);
	}

	/**
	 * 构造右外联联SQL语句.  
	 *    
	 * @param join
	 */
	public static void RIGHT_OUTER_JOIN(String join)
	{
		sql().RIGHT_OUTER_JOIN(join);
	}

	/**
	 * 构造外联SQL语句.  
	 *    
	 * @param join
	 */
	public static void OUTER_JOIN(String join)
	{
		sql().OUTER_JOIN(join);
	}

	/**
	 * 构造查询条件SQL语句.  
	 *    
	 * @param join
	 */
	public static void WHERE(String conditions)
	{
		sql().WHERE(conditions);
	}
	
	/**
	 * 构造OR SQL语句.  
	 *    
	 * @param join
	 */
	public static void OR()
	{
		sql().OR();
	}

	/**
	 * 构造AND SQL语句.  
	 *    
	 * @param join
	 */
	public static void AND()
	{
		sql().AND();
	}
	
	/**
	 * 构造分组聚合SQL语句.  
	 *    
	 * @param join
	 */
	public static void GROUP_BY(String columns)
	{
		sql().GROUP_BY(columns);
	}

	/**
	 * 构造过滤SQL语句.  
	 *    
	 * @param join
	 */
	public static void HAVING(String conditions)
	{
		sql().HAVING(conditions);
	}

	/**
	 * 构造排序SQL语句.  
	 *    
	 * @param join
	 */
	public static void ORDER_BY(String columns)
	{
		sql().ORDER_BY(columns);
	}
	
	private static SQL sql()
	{
		return localSQL.get();
	}
}
