package com.xrk.hws.common.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.naming.spi.ObjectFactory;

import com.xrk.hws.common.reflection.property.PropertyTokenizer;
import com.xrk.hws.common.reflection.wrapper.BeanWrapper;
import com.xrk.hws.common.reflection.wrapper.CollectionWrapper;
import com.xrk.hws.common.reflection.wrapper.MapWrapper;
import com.xrk.hws.common.reflection.wrapper.ObjectWrapper;
import com.xrk.hws.common.reflection.wrapper.ObjectWrapperFactory;

/**
 * 类: 元数据对象.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MetaObject
{
	private Object originalObject;
	private ObjectWrapper objectWrapper;
	private ObjectFactory objectFactory;
	private ObjectWrapperFactory objectWrapperFactory;
	private ReflectorFactory reflectorFactory;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) 
	{
	    this.originalObject = object;
	    this.objectFactory = objectFactory;
	    this.objectWrapperFactory = objectWrapperFactory;
	    this.reflectorFactory = reflectorFactory;

	    if (object instanceof ObjectWrapper) {
	      this.objectWrapper = (ObjectWrapper) object;
	    } else if (objectWrapperFactory.hasWrapperFor(object)) {
	      this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
	    } else if (object instanceof Map) {
	      this.objectWrapper = new MapWrapper(this, (Map) object);
	    } else if (object instanceof Collection) {
	      this.objectWrapper = new CollectionWrapper(this, (Collection) object);
	    } else {
	      this.objectWrapper = new BeanWrapper(this, object);
	    }
	  }	
	
	public static MetaObject forObject(Object object, ObjectFactory objectFactory,
	                                   ObjectWrapperFactory objectWrapperFactory,
	                                   ReflectorFactory reflectorFactory)
	{
		if (object == null) 
		{
			return SystemMetaObject.NULL_META_OBJECT;
		}
		else {
			return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
		}
	}
	
	public ObjectFactory getObjectFactory() {
	    return objectFactory;
	  }

	  public ObjectWrapperFactory getObjectWrapperFactory() {
	    return objectWrapperFactory;
	  }

	  public ReflectorFactory getReflectorFactory() {
		return reflectorFactory;
	  }

	  public Object getOriginalObject() {
	    return originalObject;
	  }

	  public String findProperty(String propName, boolean useCamelCaseMapping) {
	    return objectWrapper.findProperty(propName, useCamelCaseMapping);
	  }

	  public String[] getGetterNames() {
	    return objectWrapper.getGetterNames();
	  }

	  public String[] getSetterNames() {
	    return objectWrapper.getSetterNames();
	  }

	  public Class<?> getSetterType(String name) {
	    return objectWrapper.getSetterType(name);
	  }

	  public Class<?> getGetterType(String name) {
	    return objectWrapper.getGetterType(name);
	  }

	  public boolean hasSetter(String name) {
	    return objectWrapper.hasSetter(name);
	  }

	  public boolean hasGetter(String name) {
	    return objectWrapper.hasGetter(name);
	  }

	  public Object getValue(String name) {
	    PropertyTokenizer prop = new PropertyTokenizer(name);
	    if (prop.hasNext()) {
	      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
	      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
	        return null;
	      } else {
	        return metaValue.getValue(prop.getChildren());
	      }
	    } else {
	      return objectWrapper.get(prop);
	    }
	  }

	  public void setValue(String name, Object value) {
	    PropertyTokenizer prop = new PropertyTokenizer(name);
	    if (prop.hasNext()) {
	      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
	      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
	        if (value == null && prop.getChildren() != null) {
	          return;
	        } else {
	          metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
	        }
	      }
	      metaValue.setValue(prop.getChildren(), value);
	    } else {
	      objectWrapper.set(prop, value);
	    }
	  }

	  public MetaObject metaObjectForProperty(String name) {
	    Object value = getValue(name);
	    return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
	  }

	  public ObjectWrapper getObjectWrapper() {
	    return objectWrapper;
	  }

	  public boolean isCollection() {
	    return objectWrapper.isCollection();
	  }

	  public void add(Object element) {
	    objectWrapper.add(element);
	  }

	  public <E> void addAll(List<E> list) {
	    objectWrapper.addAll(list);
	  }

}
