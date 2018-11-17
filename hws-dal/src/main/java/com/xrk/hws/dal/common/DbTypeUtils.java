package com.xrk.hws.dal.common;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xrk.hws.dal.exception.DalException;

/**
 * 类: 数据类型工具.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月29日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DbTypeUtils
{
	private static Map<DbType, DbTypeMap>	dbTypeMap	= new HashMap<DbType, DbTypeMap>();

	public static Map<DbType, DbTypeMap> getDbTypeMap()
	{
		dbTypeMap = new HashMap<DbType, DbTypeMap>();
		dbTypeMap.put(DbType.SMALLINT, new DbTypeMap(Integer.TYPE, Integer.class));
		dbTypeMap.put(DbType.TINYINT, new DbTypeMap(Integer.TYPE, Integer.class));
		dbTypeMap.put(DbType.INT, new DbTypeMap(Integer.TYPE, Integer.class));
		dbTypeMap.put(DbType.BIGINT, new DbTypeMap(Long.TYPE, Long.class));
		dbTypeMap.put(DbType.FLOAT, new DbTypeMap(Float.TYPE, Float.class));
		dbTypeMap.put(DbType.DOUBLE, new DbTypeMap(Double.TYPE, Double.class));
		dbTypeMap.put(DbType.CHAR, new DbTypeMap(String.class, String.class));
		dbTypeMap.put(DbType.VARCHAR, new DbTypeMap(String.class, String.class));
		dbTypeMap.put(DbType.TEXT, new DbTypeMap(String.class, String.class));
		dbTypeMap.put(DbType.DATETIME, new DbTypeMap(Long.TYPE, Long.class));
		dbTypeMap.put(DbType.BLOB, new DbTypeMap(InputStream.class, InputStream.class));
		return dbTypeMap;
	}

	/**
	 * 由DataType获取真实类型
	 * 
	 * @param type		数据库字段类型枚举
	 *            
	 * @return Class<?> 字段类型
	 * @throws DalException
	 */
	public static Class<?> getFieldType(DbType type) throws DalException
	{
		if (type == DbType.INT || type == DbType.SMALLINT || type == DbType.TINYINT) 
		{
			return Integer.TYPE;
		}
		else if (type == DbType.BIGINT) 
		{
			return Long.TYPE;
		}
		else if (type == DbType.CHAR || type == DbType.VARCHAR || type == DbType.TEXT) 
		{
			return String.class;
		}
		else if (type == DbType.FLOAT) 
		{
			return Float.TYPE;
		}
		else if (type == DbType.DOUBLE) 
		{
			return Double.TYPE;
		}
		else if (type == DbType.DATETIME) 
		{
			return Long.TYPE;
		}
		else if (type == DbType.BLOB) 
		{
			return InputStream.class;
		}
		else 
		{
			throw new DalException(" invalid DataType: " + type.toString());
		}
	}
	
	/**
	 * 根据数据类型获取默认值.  
	 *    
	 * @param type		数据类型对象.
	 * @return
	 * @throws DalException
	 */
	public static Object getDefaultValue(DbType type) throws DalException 
	{
		if (type == DbType.INT || type == DbType.SMALLINT || type == DbType.TINYINT || type == DbType.BIGINT || type == DbType.FLOAT || type == DbType.DOUBLE) 
		{
			return 0;
		}
		else if (type == DbType.CHAR || type == DbType.VARCHAR || type == DbType.TEXT) 
		{
			return "";
		}
		else if (type == DbType.DATETIME) 
		{
			return 0;
		}
		else if (type == DbType.BLOB) 
		{
			return null;
		}
		else 
		{
			throw new DalException(" invalid DataType: " + type.toString());
		}
	}
	
	/**
	 * 字段值转换(boolean).  
	 *    
	 * @param value	数据库ResultSet返回类型.
	 * @return
	 */
	public static Object getBooleanValue(Object value)
	{
		if (Boolean.class.equals(value.getClass())) 
		{
			boolean tmp = Boolean.parseBoolean(value.toString());
			return tmp ? 1 : 0;
		}
		else 
		{
			return value;
		}
	}
	
	/**
	 * 字段值转换(时间字段)
	 * 
	 * @param value 	数据库ResultSet返回类型.
	 * @return 值
	 */
	public static Object getTimestampValue(Object value)
	{
		Class<?> type = value.getClass();
		if (java.sql.Timestamp.class.equals(type)) 
		{
			return ((java.sql.Timestamp) value).getTime();
		}
		else if (java.sql.Time.class.equals(type)) 
		{
			return ((java.sql.Time) value).getTime();
		}
		else if (java.sql.Date.class.equals(type)) 
		{
			return ((java.sql.Date) value).getTime();
		}
		else 
		{
			return value;
		}
	}
	
	public static Date getDateValue(Object value)
	{
		if(value != null)
		{
			Class<?> type = value.getClass();
			if(java.sql.Timestamp.class.equals(type))
			{
				return (Date)value;
			}
		}
		return null;
	}
	
	/**
	 * 字段值转换(Blob字段)
	 * 
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public static Object getBlobValue(Object value) throws SQLException
	{
		java.sql.Blob blob = (java.sql.Blob) value;
		return blob.getBinaryStream();
	}
	
	/**
	 * 字段值转换(Clob字段)
	 * 
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public static Object getClobValue(Object value) throws SQLException
	{
		java.sql.Clob clob = (java.sql.Clob) value;
		return clob.getCharacterStream();
	}
	
	/**
	 * 判断类型是否整数
	 * 
	 * @param type 	数据库类型DbType
	 * @return boolean
	 */
	public static boolean isInteger(DbType type)
	{
		if (type == DbType.INT || type == DbType.SMALLINT || type == DbType.BIGINT || type == DbType.TINYINT) 
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断类型是否字符串
	 * 
	 * @param type
	 *            数据库类型DbType
	 * @return
	 */
	public static boolean isString(DbType type)
	{
		if (type == DbType.CHAR || type == DbType.VARCHAR || type == DbType.TEXT) 
		{
			return true;
		}
		return false;
	}
}
