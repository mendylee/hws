package com.xrk.hws.dal.enums;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * 类: 数据类型.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月12日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public enum DBType
{
	ARRAY(Types.ARRAY),
	BIT(Types.BIT),
	TINYINT(Types.TINYINT),
	SMALLINT(Types.SMALLINT),
	INTEGER(Types.INTEGER),
	BIGINT(Types.BIGINT),
	FLOAT(Types.FLOAT),
	REAL(Types.REAL),
	DOUBLE(Types.DOUBLE),
	NUMERIC(Types.NUMERIC),
	DECIMAL(Types.DECIMAL),
	CHAR(Types.CHAR),
	VARCHAR(Types.VARCHAR),
	LONGVARCHAR(Types.LONGVARCHAR),
	DATE(Types.DATE),
	TIME(Types.TIME),
	TIMESTAMP(Types.TIMESTAMP),
	BINARY(Types.BINARY),
	VARBINARY(Types.VARBINARY),
	LONGVARBINARY(Types.LONGVARBINARY),
	NULL(Types.NULL),
	OTHER(Types.OTHER),
	BLOB(Types.BLOB),
	CLOB(Types.CLOB),
	BOOLEAN(Types.BOOLEAN),
	CURSOR(-10), // Oracle
	UNDEFINED(Integer.MIN_VALUE + 1000),
	NVARCHAR(Types.NVARCHAR), // JDK6
	NCHAR(Types.NCHAR), // JDK6
	NCLOB(Types.NCLOB), // JDK6
	STRUCT(Types.STRUCT);
    
    public final int TYPE_CODE;
	
	private static Map<Integer,DBType> codeLookup = new HashMap<Integer,DBType>();
	
	static 
	{
	    for (DBType type : DBType.values()) 
	    {
	      codeLookup.put(type.TYPE_CODE, type);
	    }
	}

	DBType(int code) 
	{
	    this.TYPE_CODE = code;
	}

	public static DBType forCode(int code)  
	{
		return codeLookup.get(code);
	}
}
