/*
 * Copyright (c) 2015-2025 by eaver Some rights reserved
 */
package eaver.cast;

/**
 * @author eaver
 * @param <S>	src object
 * @param <T>	target object
 */
public abstract class Castor<S,T> {
	

	/**
	 * src/target's property name
	 * 
	 */
	private final String property;
	
	public Castor(String property){
		if(property == null)
			throw new NullPointerException();
		this.property = property;
	}
 
	protected Object getPropertyValue(S src){
		return GetSetUtil.get(src, property);
	}
 
	protected void setPropertyValue(T target,Object value){
		GetSetUtil.set(target, property, value);
	}
	
	public String getProperty(){
		return this.property;
	}
 
	public abstract void customCast(S src,T target);
  
}
