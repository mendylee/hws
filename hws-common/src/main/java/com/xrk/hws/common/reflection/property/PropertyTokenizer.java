package com.xrk.hws.common.reflection.property;

import java.util.Iterator;

/**
 * 类: 属性分词器.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PropertyTokenizer implements Iterable<PropertyTokenizer>, Iterator<PropertyTokenizer>
{
	private String name;
	private String indexedName;
	private String index;
	private String children;
	
	public PropertyTokenizer(String fullname) 
	{
		int delim = fullname.indexOf('.');
		if (delim > -1) 
		{
			name = fullname.substring(0, delim);
			children = fullname.substring(delim + 1);
		}
		else 
		{
			name = fullname;
			children = null;
		}
		indexedName = name;
		delim = name.indexOf('[');
		if (delim > -1) 
		{
			index = name.substring(delim + 1, name.length() - 1);
			name = name.substring(0, delim);
		}
	}

	public String getName()
	{
		return name;
	}

	public String getIndex()
	{
		return index;
	}

	public String getIndexedName()
	{
		return indexedName;
	}

	public String getChildren()
	{
		return children;
	}

	@Override
	public boolean hasNext()
	{
		return children != null;
	}

	@Override
	public PropertyTokenizer next()
	{
		return new PropertyTokenizer(children);
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
	}

	@Override
	public Iterator<PropertyTokenizer> iterator()
	{
		return this;
	}
}
