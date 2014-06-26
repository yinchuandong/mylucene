package Model;

import javax.print.DocFlavor.STRING;

public class MfwHotel{
	private String id;
	private String name;
	private double lat;
	private double lng;
	private String summary;
	private String imgSrc;
	private String address;
	private String otaname;//预订的网站，如：携程网等
	private double price;
	private String bookUrl;
	private String cityId;
	private String cityName;
	
	public MfwHotel(){
		
	}
	
	public MfwHotel(String id, String name, double lat, double lng, String summary, String imgSrc,
			String address, String otaname, double price, String bookUrl, String cityId, String cityName){
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.summary = summary;
		this.imgSrc = imgSrc;
		this.address = address;
		this.otaname = otaname;
		this.price = price;
		this.bookUrl = bookUrl;
		this.cityId = cityId;
		this.cityName = cityName;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOtaname() {
		return otaname;
	}
	public void setOtaname(String otaname) {
		this.otaname = otaname;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getBookUrl() {
		return bookUrl;
	}
	public void setBookUrl(String bookUrl) {
		this.bookUrl = bookUrl;
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
	
	
	
}
