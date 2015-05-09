package eaver.cast.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eaver.cast.Cast;
import eaver.cast.Castor;
import eaver.cast.Castors;
 
public class Test {

	public static void main(String[] args) throws Throwable{
		 
		User user = createUser();  
		User1 user1 = Cast.cast(user,User1.class,new Castors<User, User1>()
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
		User user2 =  Cast.cast(user1,User.class,new Castors<User1, User>()
												.register(new Castor<User1, User>("age") { 
													@Override
													public void customCast(User1 src, User target) {
														 target.setAge((int)(src.getAge() - 100));
													}})
												.register(new Castor<User1, User>("userName") { 
													@Override
													public void customCast(User1 src, User target) {
														  target.setUserName(src.getUserName().replace("0101_", ""));
													}})
											); 
		User1 user3 = Cast.cast(user,User1.class);
		System.out.println(user);
		System.out.println(user1);
		System.out.println(user2);
		System.out.println(user3);
		  
	}
	
	private final static User createUser(){
		List<Org> orgs = new ArrayList<Org>();
		User user = new User();
		List<Short> arr = null;
		
		Org org = new Org();
		arr = new ArrayList<Short>();
		arr.add((short)1);
		arr.add((short)2);
		arr.add((short)3);
		arr.add((short)4);
		arr.add((short)5); 
		org.setArr(arr);
		org.setOrgId("1");
		org.setOrgName("org one");
		org.setRemark("test org one");
		orgs.add(org);
		
		org = new Org();
		arr = new ArrayList<Short>();
		arr.add((short)6);
		arr.add((short)7);
		arr.add((short)8);
		arr.add((short)9);
		arr.add((short)10);
		org.setArr(arr);
		org.setOrgId("2");
		org.setOrgName("org two");
		org.setRemark("test org two");
		orgs.add(org);
		
		City city = new City();
		city.setCityId("1");
		city.setCityName("changsha");
		city.setPeoples(6000000);
		city.setSqrt(5460.2);
		city.setAge(12.0);
		
	
		user.setMarr(new short[][]{{1,2,3},{4,5}});
		user.setAge(28);
		user.setBirthday(new Date());
		user.setCity(city);
		user.setOrgs(orgs);
		user.setUserId("501");
		user.setUserName("eaver");
 
		user.setAttr1("attr1");
		user.setAttr2("attr2");
		user.setAttr3("attr3");
		user.setAttr4("attr4");
		user.setAttr5("attr5");
	 
		user.setRemark1("remark1");
		user.setRemark2("remark2");
		user.setRemark3("remark3");
		user.setRemark4("remark4");
		user.setRemark5("remark5");
		return user;
	}
	
	 
	

}
