/*
 * Copyright (c) 2015-2025 by eaver Some rights reserved
 */
package eaver.cast;
 
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetSetUtil {

	
	/**
	 * read Object's property value
	 * 	Object support
	 * 		Map and POJO
	 * 		Collection which items are Map and POJO
	 * 		Array	which items are Map and POJO
	 * 	while Object is a Collection or Array,it will return the same type(Collection or Array) object which property values are already in it
	 * @param instance	Object's instance
	 * @param property	Object's property name
	 * @return
	 */
	public final static Object get(Object instance,String property){
		if(instance == null || property == null)
			return null;
		if(instance instanceof Collection) {
            Collection<?> coll = (Collection<?>)instance;
            Iterator<?> iterator =coll.iterator();
            List<Object> rs = new ArrayList<Object>();
            Object item = null;
            while(iterator.hasNext()) {
                item = iterator.next();
                rs.add(item!=null?readObjectProperty(item, property):null);
            }
            return rs;
        }
		if(instance instanceof Object[]){
            Object[] arr = (Object[])instance;
            Object[] rs = new Object[arr.length];
            for(int i=0;i<arr.length;i++)
                rs[i] = arr[i]!=null?readObjectProperty(arr[i],property):null;
            return rs;
        }
		if(instance.getClass().isArray())
			throw new GetSetException("unsupport read "+property+" in "+instance.getClass().getName());
		return readObjectProperty(instance, property);
	
	}
	
	/**
	 * write(update) Object's property's value
	 * 	Object support
	 * 		Map and POJO
	 * 		Collection which items are Map and POJO
	 * 		Array	which items are Map and POJO
	 * @param instance	Object's instance
	 * @param property	property name
	 * @param value		property's new value
	 */
	public final static void set(Object instance,String property,Object value){
		if(instance == null || property == null)
			return;
		if(instance instanceof Collection){
            Collection<?> coll = (Collection<?>)instance;
            Iterator<?> iterator =coll.iterator();
            Object item = null;
            while(iterator.hasNext()) {
                item = iterator.next();
                if(item!=null)
                    writeObjectProperty(item,property,value);
            }

		}else if(instance instanceof Object[]){
            for(Object item:(Object[])instance)
                if(item!=null)
                    writeObjectProperty(item,property,value);
		}else if(instance.getClass().isArray()){ 
				throw new GetSetException("unsupport write "+property+" in "+instance.getClass().getName());
		}else{
			writeObjectProperty(instance, property,value);
		} 
	}
	
	/**
	 * read Collection/Array 's item value
	 * @param instance
	 * @param index
	 * @return
	 */
	public final static Object getItem(Object instance,int index){
		if(instance == null||index<0)
			return null;
		if(instance instanceof List){
			List<?> list = (List<?>)instance; 
			return index<list.size()?list.get(index):null;
		}else if(instance instanceof Object[]){
			Object[] arr = (Object[])instance;
			return index<arr.length?arr[index]:null; 
		}else if(instance.getClass().isArray()){
			int length = Array.getLength(instance);
			return index<length?Array.get(instance, index):null; 
		}else{
			throw new RuntimeException("unsupport type:"+instance.getClass().getName()+",only collection and array supported.");
		}
	}
	
	/**
	 * write(update) Collection/Array 's item value
	 * @param instance
	 * @param index
	 * @param value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final static void setItem(Object instance,int index,Object value){
		if(instance == null||index<0)
			return;
		if(instance instanceof List){
			List list = (List)instance; 
			if(index<list.size())
				list.set(index, value); 
		}else if(instance instanceof Object[]){
			Object[] arr = (Object[])instance;
			if(index<arr.length)
				arr[index] = value;
		}else if(instance.getClass().isArray()){
			int length = Array.getLength(instance);
			if(index<length)
				Array.set(instance, index,Cast.cast(value,instance.getClass().getComponentType()));
		}else{
			throw new GetSetException("unsupport type:"+instance.getClass().getName()+",only collection and array supported.");
		}
	} 
	
	@SuppressWarnings("rawtypes")
	private static Object readObjectProperty(Object obj,String property){
		if(obj instanceof Map){
			return ((Map)obj).get(property);
		}
		Class<?> clazz = obj.getClass();
		GetSetBean bean = FM_CACHE.get(clazz);
		GetSetField field = bean.getGetSetField(property);
		try {
			return field!=null?field.get(obj):null;
		} catch (Throwable e) {
			throw new GetSetException(e.getMessage(),e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void writeObjectProperty(Object obj,String property,Object value){
		if(obj instanceof Map){
			((Map)obj).put(property,value);
			return;
		}
		Class<?> clazz = obj.getClass();
		GetSetBean bean = FM_CACHE.get(clazz);
		GetSetField field = bean.getGetSetField(property);
		if(field == null)
			return;
		try {
			field.setAndCast(obj,value);
		} catch (Throwable e) {
			throw new GetSetException(e.getMessage(),e);
		}
		 
	}
	
	public final static GetSetBean getGetSetBean(Class<?> clazz){
		return FM_CACHE.get(clazz);
	}
	
	protected final static Cache<Class<?>,GetSetBean> FM_CACHE = new Cache<Class<?>, GetSetBean>() {
		
		private Map<Class<?>,GetSetBean> map =  new HashMap<Class<?>,GetSetBean>();
		
		private Object lock = new Object();
		@Override
		public GetSetBean get(Class<?> key) {
			GetSetBean bean = map.get(key);
			if(bean!=null){
				return bean;
			}else{
				synchronized(lock){
					if(map.containsKey(key)){
						return map.get(key);
					}
					bean = new GetSetBean(key);
					map.put(key, bean);
					return bean;
				} 
			}
		}
		
	};
	 
}

interface Cache<K,V> {
	V get(K key);
}

class GetSetField implements GetSet{

	private Field field;
	
	private boolean typeIsCollection = false;
	private boolean typeIsArray = false;
	private Class<?> componentType = null;
	
	protected GetSetField(Field field){
		this.field = field;
		if(Collection.class.isAssignableFrom(field.getType())){
			componentType = Cast.getComponentTypeByField(field);
			typeIsCollection = true;
		}else if(field.getType().isArray()){
			componentType = field.getType().getComponentType();
			typeIsArray = true;
		}
	}

	public void set(Object instance,Object value) throws IllegalArgumentException, IllegalAccessException{
		field.set(instance, value);
	}
	
	public Object get(Object instance) throws IllegalArgumentException, IllegalAccessException{
		return field.get(instance);
	}
	
	public void setAndCast(Object instance,Object value) throws IllegalArgumentException, IllegalAccessException{
		if(value == null){
			field.set(instance,null);
			return;
		}
		Object newValue = value;
		if(typeIsCollection){
			newValue = Cast.toCollection(value,componentType);
		}else if(typeIsArray){
			newValue = Cast.toArray(value, componentType);
		}else{
			newValue = Cast.cast(value,field.getType());
		}
		field.set(instance,newValue);
	}
	
	public Object getAndCast(Object instance,Class<?> clazz) throws IllegalArgumentException, IllegalAccessException{
		return Cast.cast(field.get(instance),clazz);
	}

	@Override
	public String getName() {
		return field != null?field.getName():null;
	}

	@Override
	public Class<?> getType() {
		return field != null?field.getType():null;
	}
	
	
}

class GetSetMethod implements GetSet{

	private Method method;
	private Class<?> parameterType;
	
	/**
	 * parameterType
	 */
	private boolean typeIsCollection = false;
	private boolean typeIsArray = false;
	
	private Class<?> componentType = null;
	
	protected GetSetMethod(Method method){
		this.method = method;
		Type[] types = this.method.getParameterTypes();
		if(types!=null&&types.length>0){
			parameterType = (Class<?>)types[0];
			if(parameterType.isArray()){
				typeIsArray = true;
				componentType = parameterType.getComponentType();
			}else if(Collection.class.isAssignableFrom(parameterType)){
				typeIsCollection = true;
				componentType = Cast.getComponentTypeByMethod(method);
			}
		}
	}
	 
	public void set(Object obj,Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		method.invoke(obj,value);
	}
	 
	public Object get(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return method.invoke(obj);
	}
	
	public void setAndCast(Object obj,Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object newValue = value;
		if(typeIsCollection){
			newValue = Cast.toCollection(value, componentType);
		}else if(typeIsArray){
			newValue = Cast.toArray(value, componentType);
		}else{
			newValue = Cast.cast(value,parameterType);
		}
		method.invoke(obj,newValue);
	}
	
	public Object getAndCast(Object obj,Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		return Cast.cast(method.invoke(obj),clazz);
	}

	@Override
	public String getName() {
		if (method ==null) 
			return null;
		String name = method.getName();
		if(name.startsWith("is"))
			return name.substring(2);
		if(name.startsWith("get"))
			return name.substring(3);
		return name;
	}
	 
	public Class<?> getType(){
		if(method ==null)
			return null;
		return method.getReturnType()==null?componentType:method.getReturnType();
	}
	
}
