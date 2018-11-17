package com.xrk.hws.dal.db.mc;

import com.xrk.hws.dal.core.PhysicDatabase;

public class MemcachedDatabase extends PhysicDatabase
{
	/**
	 * 前缀，如:_
	 */
	private String prefix = null;

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
}
