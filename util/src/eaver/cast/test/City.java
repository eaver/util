package eaver.cast.test;

public class City {

	private String cityId;
	private String cityName;
	private long peoples;
	private double sqrt;
	private double age;
	
	public String toString(){
		return new StringBuilder("{")
		.append("cityId=").append(getCityId())
		.append(",cityName=").append(getCityName())
		.append(",peoples=").append(getPeoples())
		.append(",sqrt=").append(getSqrt())
		.append(",age=").append(getAge())
		.append("}")
		.toString();
	}
	
	
	public double getAge() {
		return age;
	}
	public void setAge(double age) {
		this.age = age;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public long getPeoples() {
		return peoples;
	}
	public void setPeoples(long peoples) {
		this.peoples = peoples;
	}
	public double getSqrt() {
		return sqrt;
	}
	public void setSqrt(double sqrt) {
		this.sqrt = sqrt;
	}
	
}
