package com.xrk.hws.dal.common;

/**
 * 枚举：数据类型.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public enum DbType
{
	AUTO,                
	/******with schema,like mySql need this******/
    INT,
    TINYINT,
    SMALLINT,
    BIGINT,
    CHAR,
    VARCHAR,
    FLOAT,
    DOUBLE,		
    
    /**只支持postgresql*/
    NUMBERIC,	 
    SMALLSERIAL, 
    SERIAL,		 
    BIGSERIAL,	 
    
    
    TEXT, 
    DATETIME,
    BLOB,
    CLOB,
    /***************************************/
    EXPAND,   /*to expand the object inside*/
    CLASS,
    ARRAY,
    COLLECTION
}
