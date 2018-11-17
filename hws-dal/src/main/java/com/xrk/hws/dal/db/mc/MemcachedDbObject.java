package com.xrk.hws.dal.db.mc;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xrk.hws.dal.core.Entity;
import com.xrk.hws.dal.core.EntityField;
/**
 * 
 * 类: MemcachedDbObject. <br/>  
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2014年5月25日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MemcachedDbObject
{
	/**
	 * Memcached缓存值
	 */
	private LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
	
	public LinkedHashMap<String, Object> getValues()
	{
		return values;
	}

	public void setValues(LinkedHashMap<String, Object> values)
	{
		this.values = values;
	}
	
	/**
	 * 
	 * 构建缓存数据对象  
	 *    
	 * @param dataClass		缓存数据类
	 * @param data			缓存数据
	 * @return
	 */
	public int buildObject(Entity dataClass, Object data)
	{
		if (data != null)
		{
			return this.putObject(values, dataClass, data);
		}

		return 1;
	}

	/**
	 * 
	 * 添加缓存数据  
	 *    
	 * @param holder
	 * @param dataClass
	 * @param data
	 * @return
	 */
	public int putObject(LinkedHashMap<String, Object> holder, Entity dataClass, Object data)
	{
		List<EntityField> fields = dataClass.getAllDataField();

		Object value = null;
		for (EntityField field : fields)
		{
			value = field.getValue(data);
			if (this.putDataField(holder, field, dataClass, value) != 0)
			{
				return 1;
			}
		}
		return 0;
	}

	public int putDataField(LinkedHashMap<String, Object> holder, EntityField field,
	                        Entity dataClass, Object value)
	{
		if (value == null)
		{
			if (field.isStoreNull())
			{
				holder.put(field.getColumnName(), null);
			}

			return 0;
		}

		switch (field.getDecType())
		{
			case ATOM :
				if (holder.put(field.getColumnName(), value) == null)
				{
					return 0;
				}
				return 1;
			case ARRAY :
				return this.putArray(holder, field, value);
			case COLLECTION :
				return this.putCollection(holder, field, (Collection) value);
			case CLASS :
				return this.putClass(holder, field, value);
			case MAP :
				return this.putMap(holder, field, (Map) value);
			default :
				return 1;
		}
	}

	public int putArray(LinkedHashMap<String, Object> holder, EntityField field, Object array)
	{
		int size = Array.getLength(array);
		if (size == 0)
		{
			if (field.isStoreNull())
			{
				holder.put(field.getColumnName(), null);
			}

			return 0;
		}

		Entity subClass = field.getSubClass();
		if (subClass == null)
		{
			if (holder.put(field.getColumnName(), array) == null)
			{
				return 0;
			}

			return 1;

		}

		Object[] entryDbObjects = new Object[size];
		LinkedHashMap<String, Object> eMap = null;
		for (int i = 0; i < size; i++)
		{
			eMap = new LinkedHashMap<String, Object>();
			if (this.putObject(eMap, field.getSubClass(), Array.get(array, i)) != 0)
			{
				return 1;
			}

			entryDbObjects[i] = eMap;
		}

		if (holder.put(field.getColumnName(), entryDbObjects) == null)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}

	public int putMap(LinkedHashMap<String, Object> holder, EntityField field, Map m)
	{
		if (m.size() == 0)
		{
			if (field.isStoreNull())
			{
				holder.put(field.getColumnName(), null);
			}

			return 0;
		}

		Entity subClass = field.getSubClass();
		if (subClass == null)
		{
			if (holder.put(field.getColumnName(), m) == null)
			{
				return 0;
			}

			return 1;

		}

		LinkedHashMap<String, Object> dbObject = null;
		Map<String, Object> entryDbObjects = new LinkedHashMap<String, Object>();

		for (Map.Entry entry : (Set<Map.Entry>) m.entrySet())
		{
			dbObject = new LinkedHashMap<String, Object>();

			if (this.putObject(dbObject, field.getSubClass(), entry.getValue()) != 0)
			{
				return 1;
			}

			entryDbObjects.put(entry.getKey().toString(), dbObject);
		}

		if (holder.put(field.getColumnName(), entryDbObjects) == null)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}

	public int putCollection(LinkedHashMap<String, Object> holder, EntityField field, Collection l)
	{
		if (l.size() == 0)
		{
			if (field.isStoreNull())
			{
				holder.put(field.getColumnName(), null);
			}

			return 0;
		}

		Entity subClass = field.getSubClass();
		if (subClass == null)
		{
			if (holder.put(field.getColumnName(), l) == null)
			{
				return 0;
			}

			return 1;

		}

		List<Object> entryDbObjects = new LinkedList<Object>();

		for (Object obj : l)
		{
			LinkedHashMap<String, Object> dbObject = new LinkedHashMap<String, Object>();
			if (this.putObject(dbObject, field.getSubClass(), obj) != 0)
			{
				return 1;
			}

			entryDbObjects.add(dbObject);
		}

		if (holder.put(field.getColumnName(), entryDbObjects) == null)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}

	public int putClass(LinkedHashMap<String, Object> holder, EntityField field, Object value)
	{
		Entity subDataClass = field.getSubClass();
		if (subDataClass != null)
		{
			LinkedHashMap<String, Object> subObject = new LinkedHashMap<String, Object>();
			if (this.putObject(subObject, subDataClass, value) != 0)
			{
				return 1;
			}

			if (holder.put(field.getColumnName(), subObject) == null)
			{
				return 0;
			}
		}

		return 1;
	}

	public <T> T takeObject(Entity dataClass)
	{
		if (dataClass != null)
		{
			return getObject(dataClass, this.values);
		}

		return null;
	}

	private <T> T getObject(Entity dataClass, LinkedHashMap<String, Object> storObject)
	{
		if (dataClass != null && storObject != null)
		{
			Class<?> metaClass = dataClass.getClassMeta();
			List<EntityField> fields;

			fields = dataClass.getAllDataField();
			Object data;
			try
			{
				data = metaClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e1)
			{
				e1.printStackTrace();
				return null;
			}

			for (EntityField field : fields)
			{
				if (this.getDataField(storObject, field, data) != 0)
				{
					return null;
				}
			}

			return (T) data;
		}

		return null;
	}

	private int getDataField(LinkedHashMap<String, Object> holder, EntityField field,Object mother)
	{
		Object value = null;
		value = holder.get(field.getColumnName());

		if (value == null)
		{
			return 0;
		}

		if (value instanceof LinkedHashMap) /* not atom */
		{
			switch (field.getDecType())
			{
				case ARRAY :
					if (this.getArray((LinkedHashMap<String, Object>) value, field, mother) != 0)
					{
						return 1;
					}
					break;
				case COLLECTION :
					if (this.getCollection((LinkedHashMap<String, Object>) value, field, mother) != 0)
					{
						return 1;
					}
					break;
				case MAP :
					if (this.getMap((LinkedHashMap<String, Object>) value, field, mother) != 0)
					{
						return 1;
					}
					break;

				case CLASS :
					if (this.getClass((LinkedHashMap<String, Object>) value, field, mother) != 0)
					{
						return 1;
					}
					break;

				default :
					return 1;
			}

			return 0;
		}
		else
		/* basic object,set directly */
		{
			return field.setValue(mother, value);
		}
	}

	private int getArray(LinkedHashMap<String, Object> holder, EntityField field, Object mother)
	{
		Object eStore = null;
		Object eData = null;
		Collection sColl = (Collection) holder;
		Object vArray = null;
		int size;

		vArray = field.getValue(mother);
		if (vArray != null && !vArray.getClass().isArray())
		{
			return 1;
		}

		Entity subClass = field.getSubClass();
		size = sColl.size();
		if (vArray == null || Array.getLength(vArray) != size)
		{
			vArray = Array.newInstance(field.getFiedMeta().getType().getComponentType(), size);
			if (field.setValue(mother, vArray) != 0)
			{
				return 1;
			}
		}

		int i = 0;
		Iterator it = sColl.iterator(); // 获得一个迭代子
		while (it.hasNext())
		{
			eStore = it.next(); // 得到下一个元素

			if (subClass == null) // load entry data
			{
				Array.set(vArray, i, eStore);
			}
			else
			{
				eData = this.getObject(subClass, (LinkedHashMap<String, Object>) eStore);
				Array.set(vArray, i, eData);
			}
		}

		return 0;
	}

	private int getMap(LinkedHashMap<String, Object> holder, EntityField field, Object mother)
	{
		Object eStore = null;
		Object eData = null;
		Map vMap = null;

		vMap = (Map) field.getValue(mother);
		if (vMap == null)
		{
			return 1;
		}

		Entity subClass = field.getSubClass();

		for (Map.Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) holder.entrySet())
		{

			eStore = entry.getValue();

			if (subClass == null)
			{
				vMap.put(entry.getKey(), eStore);
			}
			else
			{
				eData = this.getObject(subClass, (LinkedHashMap<String, Object>) eStore);
				if (eData != null)
				{
					vMap.put(entry.getKey(), eData);
				}
			}
		}

		return 0;
	}

	private int getCollection(LinkedHashMap<String, Object> holder, EntityField field,
	                          Object mother)
	{
		Object eStore = null;
		Object eData = null;
		Collection sColl = (Collection) holder;
		Collection vColl = null;

		vColl = (Collection) field.getValue(mother);
		if (vColl == null)
		{
			return 1;
		}

		Entity subClass = field.getSubClass();

		Iterator it = sColl.iterator(); // 获得一个迭代子
		while (it.hasNext())
		{
			eStore = it.next(); // 得到下一个元素

			if (subClass == null) // load entry data
			{
				vColl.add(eStore);
			}
			else
			// load entry class
			{
				eData = this.getObject(subClass, (LinkedHashMap<String, Object>) eStore);
				if (eData != null)
				{
					vColl.add(eData);
				}
			}
		}

		return 0;
	}

	private int getClass(LinkedHashMap<String, Object> holder, EntityField field, Object mother)
	{
		Object eData = null;

		Entity subClass = field.getSubClass();

		if (subClass != null)
		{
			eData = this.getObject(subClass, holder);

			if (field.setValue(mother, eData) != 0)
			{
				return 1;
			}
		}
		else
		{
			return 1;
		}

		return 0;
	}

}
