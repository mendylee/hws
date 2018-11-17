package com.xrk.hws.dal.enums;

/**
 * 枚举: 分库规则
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-5-30
 * <br>==========================
 */
public enum ShardType 
{
    Range,      	// 范围分库
    Hash,        	// 取模分库
    None,       	// 不分库
    Hash_str,  		// 字符串取模分库，只在用户中心下沉时使用
    RangeHash,  	// 范围+hash分库
    Hash_string,  	//字符串取模分库
}
