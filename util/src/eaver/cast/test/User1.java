package eaver.cast.test;

import java.util.Arrays;


public class User1 extends UserBase{

	private String userName;
	private long age;
	private String birthday;
	private City1 city; 
	private Org1[] orgs; 
	private int[][] marr; 
	private String attr1;
	private String attr2;
	private String attr3; 
	private String remark1;
	private String remark2;
	private String remark3;
	
	public String toString(){
		return new StringBuilder("{")
		.append("userId=").append(getUserId())
		.append(",userName=").append(getUserName())
		.append(",age=").append(getAge())
		.append(",birthday=").append(getBirthday())
		.append(",attr1=").append(getAttr1())
		.append(",attr2=").append(getAttr2())
		.append(",attr3=").append(getAttr3())
		.append(",remark1=").append(getRemark1())
		.append(",remark2=").append(getRemark2())
		.append(",remark3=").append(getRemark3())
		.append(",city=").append(getCity())
		.append(",marr=").append(joinArrs(getMarr()))
		.append(",orgs=").append(joinArrs(getOrgs()))
		.append("}")
		.toString(); 
	}
	
	private String joinArrs(int[][] arrs){
		StringBuilder sb = new StringBuilder("[");
		for(int i=0;i<arrs.length;i++){
			sb.append(Arrays.toString(arrs[i])).append(i<arrs.length-1?",":"");
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String joinArrs(Org1[] arr){
		StringBuilder sb = new StringBuilder("[");
		for(int i=0;i<arr.length;i++){
			sb.append(arr[i]).append(i<arr.length-1?",":"");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getAge() {
		return age;
	}
	public void setAge(long age) {
		this.age = age;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public City1 getCity() {
		return city;
	}
	public void setCity(City1 city) {
		this.city = city;
	}
	public Org1[] getOrgs() {
		return orgs;
	}
	public void setOrgs(Org1[] orgs) {
		this.orgs = orgs;
	}
	public int[][] getMarr() {
		return marr;
	}
	public void setMarr(int[][] marr) {
		this.marr = marr;
	}
	public String getAttr1() {
		return attr1;
	}
	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}
	public String getAttr2() {
		return attr2;
	}
	public void setAttr2(String attr2) {
		this.attr2 = attr2;
	}
	public String getAttr3() {
		return attr3;
	}
	public void setAttr3(String attr3) {
		this.attr3 = attr3;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	public String getRemark3() {
		return remark3;
	}
	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}
 
	
	 
}
