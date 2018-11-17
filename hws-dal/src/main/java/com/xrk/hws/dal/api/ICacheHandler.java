package com.xrk.hws.dal.api;

import java.util.List;

import com.xrk.hws.dal.exception.DalException;

/**
 * 接口: 缓存处理器接口.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月12日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface ICacheHandler
{
	/**
	 * 根据给定的参数获取缓存数据.  
	 *    
	 * @param key			缓存key.
	 * @param prefix		缓存前缀.
	 * @return				缓存数据.
	 * @throws DalException
	 * @throws Exception
	 */
	public byte[] get(String key, String prefix) throws DalException, Exception;

	/**
	 * 批量获取缓存数据.  
	 *    
	 * @param keys			缓存key列表.
	 * @param prefix		缓存前缀列表.
	 * @return				缓存数据.
	 * @throws DalException
	 * @throws Exception
	 */
	public List<byte[]> getMulti(List<String> keys, String prefix) throws DalException, Exception;

	/**
	 * 向缓存中添加数据.  
	 *    
	 * @param key			缓存key.
	 * @param buff			缓存数据.
	 * @param prefix		缓存前缀.
	 * @throws DalException
	 * @throws Exception
	 */
	public void set(String key, byte[] buff, String prefix) throws DalException, Exception;

	/**
	 * 批量缓存数据.  
	 *    
	 * @param keys			缓存key列表.
	 * @param buffs			缓存数据列表.
	 * @param prefix		缓存前缀.
	 * @throws DalException
	 * @throws Exception
	 */
	public void setMulti(List<String> keys, List<byte[]> buffs, String prefix) throws DalException,Exception;

	/**
	 * 删除缓存数据.  
	 *    
	 * @param key			缓存key.
	 * @param prefix		缓存前缀.
	 * @throws DalException
	 * @throws Exception
	 */
	public void delete(String key, String prefix) throws DalException, Exception;

	/**
	 * 批量删除缓存数据.  
	 *    
	 * @param keys			缓存key列表.
	 * @param prefix		缓存前缀列表.
	 * @throws DalException
	 * @throws Exception
	 */
	public void deleteMulti(List<String> keys, List<String> prefixes) throws DalException, Exception;
}
