package Util;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;

import Model.Hotel;

public class HotelUtil {

	/**
	 * 从数据库中获取所有的酒店信息
	 * @return
	 */
	public static HashMap<String, Hotel> getAllHotel(){
		HashMap<String, Hotel> result = new HashMap<String, Hotel>();
		
		String sql = "SELECT b.sid, b.uid, AVG(b.price) as min_price FROM t_baiduhotel as b GROUP BY b.sid";
		ResultSet set = DbUtil.executeQuery(sql, null);
		try {
			while(set.next()){
				String sid = set.getString("sid");
				String uid = set.getString("uid");
				double price = set.getDouble("min_price");
				Hotel hotel = new Hotel();
				hotel.setSid(sid);
				hotel.setUid(uid);
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
	public static void main(String[] args) throws IOException{
		getAllHotel();
	}
}
