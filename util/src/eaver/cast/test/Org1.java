package eaver.cast.test;

import java.util.Arrays;

public class Org1 {
	
	private String orgId;
	private String orgName;
	private String remark;
	private long[] arr;
	public String toString(){
		return new StringBuilder("{")
		.append("orgId=").append(getOrgId())
		.append(",orgName=").append(getOrgName())
		.append(",remark=").append(getRemark())
		.append("arr=").append(Arrays.toString(getArr()))
		.append("}").toString();
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public long[] getArr() {
		return arr;
	}
	public void setArr(long[] arr) {
		this.arr = arr;
	}
 
	
	

}
