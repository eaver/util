package eaver.cast.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class User extends UserBase{ 
	private String userName;
	private int age;
	private Date birthday;
	private City city; 
	private Collection<Org> orgs; 
	private short[][] marr; 
	private String attr1;
	private String attr2;
	private String attr3;
	private String attr4;
	private String attr5; 
	private String remark1;
	private String remark2;
	private String remark3;
	private String remark4;
	private String remark5;
	
	public String toString(){
		return new StringBuilder("{")
		.append("userId=").append(getUserId())
		.append(",userName=").append(getUserName())
		.append(",age=").append(getAge())
		.append(",birthday=").append(getBirthday())
		.append(",attr1=").append(getAttr1())
		.append(",attr2=").append(getAttr2())
		.append(",attr3=").append(getAttr3())
		.append(",attr4=").append(getAttr4())
		.append(",attr5=").append(getAttr5())
		.append(",remark1=").append(getRemark1())
		.append(",remark2=").append(getRemark2())
		.append(",remark3=").append(getRemark3())
		.append(",remark4=").append(getRemark4())
		.append(",remark5=").append(getRemark5())
		.append(",city=").append(getCity())
		.append(",marr=").append(joinArrs(getMarr()))
		.append(",orgs=").append(getOrgs())
		.append("}")
		.toString(); 
	}
	
	private String joinArrs(short[][] arrs){
		StringBuilder sb = new StringBuilder("[");
		for(int i=0;i<arrs.length;i++){
			sb.append(Arrays.toString(arrs[i])).append(i<arrs.length-1?",":"");
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
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	public Collection<Org> getOrgs() {
		return orgs;
	}
	public void setOrgs(Collection<Org> orgs) {
		this.orgs = orgs;
	}
	public short[][] getMarr() {
		return marr;
	}
	public void setMarr(short[][] marr) {
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
	public String getAttr4() {
		return attr4;
	}
	public void setAttr4(String attr4) {
		this.attr4 = attr4;
	}
	public String getAttr5() {
		return attr5;
	}
	public void setAttr5(String attr5) {
		this.attr5 = attr5;
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
	public String getRemark4() {
		return remark4;
	}
	public void setRemark4(String remark4) {
		this.remark4 = remark4;
	}
	public String getRemark5() {
		return remark5;
	}
	public void setRemark5(String remark5) {
		this.remark5 = remark5;
	} 
	
	
}
