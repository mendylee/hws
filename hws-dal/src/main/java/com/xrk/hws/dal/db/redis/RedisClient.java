package com.xrk.hws.dal.db.redis;

import com.xrk.hws.dal.common.ClustClient;

/**
 * 类: Redis客户端.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月22日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class RedisClient extends ClustClient
{
	@Override
	public int toBeReady()
	{
		return 0;
	}

	@Override
	public int close()
	{
		return 0;
	}
}
