/*
 * Copyright (c) 2015-2025 by eaver Some rights reserved
 */
package eaver.cast;

public class GetSetException extends RuntimeException{
 
	private static final long serialVersionUID = 1L;

	public GetSetException(String message,Throwable e){
		super(message,e);
	}
	
	public GetSetException(String message){
		super(message);
	}
	
}
