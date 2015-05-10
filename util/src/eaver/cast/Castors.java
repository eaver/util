/*
 * Copyright (c) 2015-2025 by eaver Some rights reserved
 */
package eaver.cast;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 	use case:
		User user = createUser(); 
		User1 user1 = null;
		user1 = Cast.cast(user,User1.class,new Castors<User, User1>()
										.register(new Castor<User, User1>("age") { 
											@Override
											public void customCast(User src, User1 target) {
												  target.setAge(100+src.getAge());
											}})
										.register(new Castor<User, User1>("userName") { 
											@Override
											public void customCast(User src, User1 target) {
												  target.setUserName("0101_"+src.getUserName());
											}})
									); 
		System.out.println(JSON.toJSONString(user1)); 
 * @see Castor
 * @author eaver
 * @param <S>
 * @param <T>
 */
public class Castors<S,T>{
 
	public Castors<S,T> register(Castor<S,T> castor){
		if(castor == null)
			throw new NullPointerException();
		castors.put(castor.getProperty(),castor);
		return this;
	}
	
	private Map<String,Castor<S,T>> castors = new HashMap<String,Castor<S,T>>();

	public boolean match(String property){
		return castors.containsKey(property);
	}
 
	public void customCast(String property,S src,T target){
		if(castors.containsKey(property)){
			castors.get(property).customCast(src, target);
		}else{
			throw new RuntimeException("custom cast,not matched:"+property);
		}
	}
}
