package Util;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import Model.Hotel;

public class HotelUtil {

	/**
	 * 从数据库中获取所有的酒店信息
	 * @return
	 */
	public static HashMap<String, Hotel> getAllHotel(){
		HashMap<String, Hotel> result = new HashMap<String, Hotel>();
		
		String sql = "SELECT b.sid, b.uid, b.hotel_name, b.hotel_address, b.phone, b.pic, MIN(b.price) as min_price FROM t_baiduhotel as b GROUP BY b.sid";
		ResultSet set = DbUtil.executeQuery(sql, null);
		try {
			while(set.next()){
				String sid = set.getString("sid");
				String uid = set.getString("uid");
				String hotelName = set.getString("hotel_name");
				String hotelAddress = set.getString("hotel_address");
				String phone = set.getString("phone");
				String pic = set.getString("pic");
				double price = set.getDouble("min_price");
				price = (price < 80.0) ? 80.0 : price;
				Hotel hotel = new Hotel();
				hotel.setSid(sid);
				hotel.setUid(uid);
				hotel.setHotelName(hotelName);
				hotel.setHotelAddress(hotelAddress);
				hotel.setPhone(phone);
				hotel.setPic(pic);
				hotel.setPrice(price);
				result.put(sid, hotel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close();
		}
		return result;
	}
	
	/**
	 * 获得该景点下的酒店列表
	 * @param psid
	 * @return
	 */
	public static ArrayList<Hotel> getSceneHotel(String psid){
		ArrayList<Hotel> result = new ArrayList<Hotel>();
		String sql = "SELECT b.sid, b.uid, b.hotel_name, b.hotel_address, b.phone, b.price, b.pic, b.comment_score FROM t_baiduhotel as b where b.sid=?";
		String[] params = {psid};
		ResultSet set = DbUtil.executeQuery(sql, params);
		try {
			while(set.next()){
				String sid = set.getString("sid");
				String uid = set.getString("uid");
				String hotelName = set.getString("hotel_name");
				String hotelAddress = set.getString("hotel_address");
				String phone = set.getString("phone");
				String pic = set.getString("pic");
				double commentScore = set.getDouble("comment_score");
				double price = set.getDouble("price");
				
				Hotel hotel = new Hotel();
				hotel.setSid(sid);
				hotel.setUid(uid);
				hotel.setHotelName(hotelName);
				hotel.setHotelAddress(hotelAddress);
				hotel.setPhone(phone);
				hotel.setPic(pic);
				hotel.setCommentScore(commentScore);
				hotel.setPrice(price);
				result.add(hotel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close();
		}
		return result;
	}
	
	
	public static void main(String[] args) throws IOException{
		long begin = System.currentTimeMillis();
		getAllHotel();
		long end = System.currentTimeMillis();
		long delay = end - begin;
		System.out.println("耗时：" + delay + "ms");
	}
}
