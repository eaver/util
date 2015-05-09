package eaver.cast.test;

import java.util.List;

public class Org {
	
	private String orgId;
	private String orgName;
	private String remark;
	private List<Short> arr;
	public String toString(){
		return new StringBuilder("{")
		.append("orgId=").append(getOrgId())
		.append(",orgName=").append(getOrgName())
		.append(",remark=").append(getRemark())
		.append("arr=").append(getArr())
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
	public List<Short> getArr() {
		return arr;
	}
	public void setArr(List<Short> arr) {
		this.arr = arr;
	}
 
	

}
