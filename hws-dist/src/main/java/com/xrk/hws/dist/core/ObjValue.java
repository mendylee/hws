package com.xrk.hws.dist.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.xrk.hws.dist.common.ParkStatg;

/**
 * ObjValue: ObjValue.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月2日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ObjValue extends LinkedHashMap implements ParkStatg
{
	private static final long serialVersionUID = 4128173936045517629L;

	public void setString(String keyStr, String valueStr)
	{
		super.put(keyStr, valueStr);
	}

	public String getString(String keyStr)
	{
		return (String) super.get(keyStr);
	}

	public int getStringInt(String keyStr)
	{
		return Integer.parseInt(getString(keyStr));
	}

	public ObjValue getWidely(String widelykey)
	{
		ObjValue obj = new ObjValue();
		for (Iterator iter = this.keySet().iterator(); iter.hasNext();)
		{
			String curkey = (String) iter.next();
			if (Pattern.matches(widelykey, curkey))
			{
				obj.put(curkey, this.getObj(curkey));
			}
		}
		return obj;
	}

	public ObjValue removeWidely(String widelykey)
	{
		ObjValue obj = new ObjValue();

		List<String> keylist = new ArrayList<String>();
		for (Iterator iter = this.keySet().iterator(); iter.hasNext();)
		{
			String curkey = (String) iter.next();
			if (Pattern.matches(widelykey, curkey))
			{
				keylist.add(curkey);
			}
		}

		for (String ck : keylist)
		{
			Object rvobj = this.remove(ck);
			if (rvobj != null)
			{
				obj.put(ck, rvobj);
			}
		}

		return obj;
	}

	public void setObj(String keyStr, Object valueObj)
	{
		super.put(keyStr, valueObj);
	}

	public Object getObj(String keyStr)
	{
		return super.get(keyStr);
	}

	public ArrayList getObjNames()
	{
		ArrayList al = new ArrayList();
		for (Iterator iter = this.keySet().iterator(); iter.hasNext();)
		{
			al.add(iter.next());
		}

		return al;
	}

	public ArrayList getObjValues()
	{
		ArrayList al = new ArrayList();
		for (Iterator iter = this.values().iterator(); iter.hasNext();)
		{
			al.add(iter.next());
		}

		return al;
	}
	
	public static void main(String[] args)
	{
		ObjValue ov = new ObjValue();
		ov.setString("1","1");
		ov.setString("1.1","11");
		ov.setString("1.1.1","1111");
		ov.setString("1.1.1.1","1111");
		ov.setString("1.2","12");
		ov.setString("1.2.1","121");
		ov.setString("2","2");
		ov.setString("22","22");
		ov.setString("domain","1");
		ov.setString("domain._me_ta.version","1");
		ov.setString("domain.node1","1");
		ov.setString("domain.node1._me_ta.version","1");
		ov.setString("domain.node2","1");
		ov.setString("domain.node2._me_ta.version","1");
		ov.setString("domain.node2.a","1");
		ov.setString("domain.node2.a._me_ta.version","1");
		System.out.println(ov.getWidely("1.[^.]*.1"));
		System.out.println(ov.getWidely("1.1.*"));
		System.out.println(ov.getWidely("2\\w"));
		System.out.println(ov.getWidely("domain..*._me_ta.version"));
		System.out.println(ov.getWidely("domain.[^_me_ta]*"));
		String a = "domain.node1._me_ta.version";
		System.out.println(a.substring(0,a.indexOf("._me_ta.")));
	}
}