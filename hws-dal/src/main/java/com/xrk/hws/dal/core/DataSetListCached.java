package com.xrk.hws.dal.core;

/**
 * 
 * 类: 数据集列表缓存. <br/>  
 * <p>
 * 	它提供了对于列表数据的自动缓存机制，通常情况根据我们的结果集配置文件开关，即可实现对列表数据的缓存机制。
 *  另外，因为考虑到所谓的行缓存(RowCache)和列缓存(ListCache)更多的时候是由业务层面决定的。所以
 *  在考虑缓存数据时要慎重考虑具体的应用场景。比如：对于一些操作非常频繁的热点数据(热数据)，此时可以考虑
 *  将这些key值通过列表缓存起来，这样做的目的有助于提高系统性能。降低对数据库的访问压力。当然，后续会逐渐
 *  加入读写分离的机制。
 * </p>
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DataSetListCached extends DataSet
{

}