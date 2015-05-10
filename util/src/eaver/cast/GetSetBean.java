/*
 * Copyright (c) 2015-2025 by eaver Some rights reserved
 */
package eaver.cast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class GetSetBean {

	private final Map<String, GetSet> getGetFields = new HashMap<String, GetSet>();
	private final Map<String, GetSet> lowerKeyGetSetFields = new HashMap<String, GetSet>();

	private final Map<String, GetSet> setMethods = new HashMap<String,GetSet>();
	private final Map<String, GetSet> getMethods = new HashMap<String,GetSet>();
	private final Map<String, GetSet> lowerKeySetMethods = new HashMap<String,GetSet>();
	private final Map<String, GetSet> lowerKeyGetMethods = new HashMap<String,GetSet>();
	
	/**
	 * original field
	 * key:field name
	 * value:Field
	 */
	private final Map<String,Field> fields = new HashMap<String,Field>();
	/**
	 * lower key : original field
	 * key: lower field name
	 * value:field name
	 */
	private final Map<String,String> lowerKeyFields = new HashMap<String,String>();

	protected GetSetBean(Class<?> clazz) {
		if (clazz == null)
			return;
		 Map<String,Field> fields = getFieldsIncludeParent(clazz);  
		if (fields != null && fields.size()>0) {
			for (Field f:fields.values()) {
				f.setAccessible(true);
				this.fields.put(f.getName(),f);
				this.lowerKeyFields.put(f.getName().toLowerCase(),f.getName());
				GetSet field = wrapperField(f);
				this.getGetFields.put(f.getName(),field);
				this.lowerKeyGetSetFields.put(f.getName().toLowerCase(),field);
			}
		}
		Map<String,Method> methods = getMethodsIncludeParent(clazz);
		if (methods != null && methods.size()>0) {
			String methodName = null;
			int index = 0;
			for (Method m:methods.values()) {
				methodName = m.getName();
				if (m.getParameterTypes() == null || m.getParameterTypes().length == 0) {
					if (methodName.startsWith("get")) {
						index = 3;
					} else if (methodName.startsWith("is")) {
						index = 2;
					} else {
						continue;
					}
					methodName = methodName.substring(index);
					if (methodName.length() > 0) {
						m.setAccessible(true);
						GetSet getSetMethod = wrapperMethod(m);
						methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
						getMethods.put(methodName, getSetMethod);
						lowerKeyGetMethods.put(methodName.toLowerCase(), getSetMethod);
					}
				} else if (m.getParameterTypes()!=null&&m.getParameterTypes().length== 1) {
					if (methodName.startsWith("set")) {
						methodName = methodName.substring(3);
						if (methodName.length() > 0) {
							m.setAccessible(true);
							GetSet getSetMethod = wrapperMethod(m);
							methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
							setMethods.put(methodName, getSetMethod);
							lowerKeySetMethods.put(methodName.toLowerCase(),getSetMethod);
						}
					}
				}
			}
		}
	}
	
	public Map<String,String> getLowerKeyFieldMap(){
		return this.lowerKeyFields;
	}
	
	public Map<String,Field> getFields(){
		return this.fields;
	}

	public Map<String, GetSet> getSetMethods() {
		return setMethods;
	}

	public Map<String, GetSet> getGetMethods() {
		return getMethods;
	}

	public Map<String, GetSet> getLowerKeySetMethods() {
		return lowerKeySetMethods;
	}

	public Map<String, GetSet> getLowerKeyGetMethods() {
		return lowerKeyGetMethods;
	}

	public Map<String,GetSet> getLowerKeyFields() {
		return lowerKeyGetSetFields;
	}

	public GetSet getGetMethod(String fieldName){
		return getMethods.get(fieldName);
	}

	public GetSet getSetMethod(String fieldName){
		return setMethods.get(fieldName);
	}

	public GetSetField getGetSetField(String fieldName) {
		return (GetSetField)getGetFields.get(fieldName);
	}

	public Map<String,GetSet> getGetSetFields() {
		return getGetFields;
	}

	private GetSet wrapperMethod(Method method) {
		return new GetSetMethod(method);
	}

	private GetSet wrapperField(Field field) {
		return new GetSetField(field);
	}
 
	private static Map<String, Field> getFieldsIncludeParent(Class<?> type) {
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		Map<String, Field> fields = null;
		while (type != Object.class) {
			fields = getFields(type);
			for (Field field : fields.values()) {
				if (fieldMap.containsKey(field.getName())) 
					continue;
				fieldMap.put(field.getName(), field);
			}
			type = type.getSuperclass();
		}
		return fieldMap;
	}
 
	private static Map<String, Field> getFields(Class<?> type) {
		Field[] fields = type.getDeclaredFields();
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		for (Field field : fields) {
			fieldMap.put(field.getName(), field);
		}
		return fieldMap;
	}
 
	private static Map<String, Method> getMethodsIncludeParent(Class<?> type) {
		Map<String, Method> methodMap = new HashMap<String, Method>();
		Map<String, Method> methods = null;
		while (type != Object.class) {
			methods = getMethods(type);
			for (Method method : methods.values()) {
				if (methodMap.containsKey(method.getName())) 
					continue;
				methodMap.put(method.getName(), method);
			}
			type = type.getSuperclass();
		}
		return methodMap;
	}
	
	private static Map<String, Method> getMethods(Class<?> type) {
		Method[] methods = type.getDeclaredMethods();
		Map<String, Method> methodMap = new HashMap<String, Method>();
		for (Method method : methods) {
			//need only get set method
			if(Modifier.isPrivate(method.getModifiers()) ||Modifier.isStatic(method.getModifiers()))
				continue;
			//need only get set method
			if(method.getParameterTypes()!=null&&method.getParameterTypes().length>1)
				continue;
			methodMap.put(method.getName(), method);
		}
		return methodMap;
	}
}