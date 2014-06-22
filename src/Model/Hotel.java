package Model;

import java.util.Comparator;

/**
 * 酒店的实体模型
 * @author yinchuandong
 *
 */
public class Hotel implements Comparable<Hotel>{

	private String sid;
	private String uid;
	private double mapX;
	private double mapY;
	private double price;
	
	
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public double getMapX() {
		return mapX;
	}
	public void setMapX(double mapX) {
		this.mapX = mapX;
	}
	public double getMapY() {
		return mapY;
	}
	public void setMapY(double mapY) {
		this.mapY = mapY;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public int compareTo(Hotel o) {
		if (this.getPrice() > o.getPrice()) {
			return 1;
		}else{
			return -1;
		}
	}
	
	
}
