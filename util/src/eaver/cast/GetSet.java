/*
 * Copyright (c) 2015-2025 by eaver Some rights reserved
 */
package eaver.cast;

import java.lang.reflect.InvocationTargetException;

/**
 * method and field 's wrap
 * @author eaver
 */
public interface GetSet {
	void 		set(Object instance, Object value) 				throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	Object 	get(Object instance)										throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	void		setAndCast(Object instance, Object value)	throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	Object 	getAndCast(Object instance, Class<?> clazz)	throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	String 	getName();
	Class<?>getType();
}