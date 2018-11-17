package com.xrk.hws.dal.core;

import com.xrk.hws.dal.common.Database;

/**
 * 类: 虚拟数据库存储. <br/>  
 * <p>
 * 	这是一个抽象的类，它提供了对数据逻辑上的一种高度抽象，单纯是从逻辑上的一种概念。
 * 	实际其内部实现而言，其实是对物理存储上的一种聚合关系，是物理数据存储的集合概念。
 * 	目前不支持这种存储方式。待后续有时间再实现此种方式的存储
 * </p>
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class VirtualDatabase extends Database
{

}
