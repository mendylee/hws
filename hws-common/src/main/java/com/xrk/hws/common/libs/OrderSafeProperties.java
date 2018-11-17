package com.xrk.hws.common.libs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.oval.internal.util.LinkedSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * 类：一个带安全排序的属性类.
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月13日
 * <br>==========================
 */
public class OrderSafeProperties extends java.util.Properties
{
    private static final long serialVersionUID = 1L;
    
	private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    @Override
	public void load(InputStream inputStream) throws IOException
	{

		List<String> lines = IOUtils.readLines(inputStream, "utf-8");
		IOUtils.closeQuietly(inputStream);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (String line : lines) 
		{
			line = line.replaceAll("\\\\\"", "\"").replaceAll("(^|[^\\\\])(\\\\')", "$1'");

			String escapedLine = StringEscapeUtils.escapeJava(line) + "\n";
			escapedLine = escapedLine.replaceAll("\\\\\\\\", "\\\\");
			out.write(escapedLine.getBytes("iso-8859-1"));
		}
		super.load(new ByteArrayInputStream(out.toByteArray()));
	}
    
    public Set<Object> keySet() 
    {
        return keys;
    }

	@Override
	public Object put(Object key, Object value)
	{
		keys.add(key);
		return super.put(key, value);
	}

	@Override
	public Object remove(Object o)
	{
		keys.remove(o);
		return super.remove(o);
	}

	@Override
	public void clear()
	{
		keys.clear();
		super.clear();
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> map)
	{
		keys.addAll(map.keySet());
		super.putAll(map);
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet()
	{
		Set<Map.Entry<Object, Object>> entrySet = new LinkedSet<Map.Entry<Object, Object>>(keys.size());
		for (Object key : keys) 
		{
			entrySet.add(new Entry(key, get(key)));
		}

		return entrySet;
	}

    /**
     * A map entry (key-value pair).
     */
	static class Entry implements Map.Entry<Object, Object>
	{
		/**
		 * Returns the key corresponding to this entry.
		 */
		private final Object key;
		/**
		 * Returns the value corresponding to this entry.
		 */
		private final Object value;

		/**
		 * Creates a new instance of Entry.  
		 *  
		 * @param key
		 * @param value
		 */
		private Entry(Object key, Object value) 
		{
			this.key = key;
			this.value = value;
		}

		/**
		 * @see java.util.Map.Entry#getKey()
		 */
		public Object getKey()
		{
			return key;
		}

		/**
		 * @see java.util.Map.Entry#getValue()
		 */
		public Object getValue()
		{
			return value;
		}

		/**
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		public Object setValue(Object o)
		{
			throw new IllegalStateException("not implemented");
		}
	}

}
