package Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.print.attribute.standard.Sides;

import Model.MfwHotel;
import Model.Scenery;
import Util.DbUtil;

/**
 * 计算景点和和酒店的对应关系
 * @author yinchuandong
 *
 */
public class SceneryHotelLinker {
	
	private ArrayList<MfwHotel> hotelList;
	private ArrayList<Scenery> sceneryList;

	private double[][] distance;
	private ArrayList<MfwHotel> bestHotelList;
	
	public SceneryHotelLinker(){
		hotelList = new ArrayList<MfwHotel>();
		sceneryList = new ArrayList<Scenery>();
		bestHotelList = new ArrayList<MfwHotel>();
		init();
	}
	
	private void init(){
		try {
			selectHotel("广州");
			selectScenery("广州");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int sLen = sceneryList.size();
		int hLen = hotelList.size();
		distance = new double[sLen][hLen];
		for (int i = 0; i < sceneryList.size(); i++) {
			Scenery scenery = sceneryList.get(i);
			double minDistance = Double.MAX_VALUE;
			double minPrice = Double.MAX_VALUE;
			int minId = 0;
			for (int j = 0; j < hotelList.size(); j++) {
				MfwHotel hotel = hotelList.get(j);
				double sLat = scenery.getMapX();
				double sLng = scenery.getMapY();
				double hLat = hotel.getLat();
				double hLng = hotel.getLng();
				
				double tmp = Math.sqrt((sLat - hLat)*(sLat - hLat) + (sLng - hLng)*(sLng - hLng));
				distance[i][j] = tmp;
				if(tmp < 0.01 && minPrice > hotel.getPrice()){
					minDistance = tmp;
					minPrice = hotel.getPrice();
					minId = j;
				}
//				else if (minDistance > tmp) {
//					minDistance = tmp;
//					minId = j;
//				}
			}
			MfwHotel bestH = hotelList.get(minId);
			bestHotelList.add(bestH);
			System.out.print(scenery.getAmbiguitySname() + "-->" + bestH.getName() + "-->");
			System.out.print(bestH.getPrice()+"-->距离：" + minDistance);
			System.out.println();
		}
		
		System.out.println();
		
	}
	
	/**
	 * 从数据库中选择城市的酒店
	 * @param pCityName 城市的名称，如：广州
	 * @throws SQLException
	 */
	private void selectHotel(String pCityName) throws SQLException{
		String sql = "select * from t_hotel where cityName=?";
		String[] params = {pCityName};
		ResultSet resultSet = DbUtil.executeQuery(sql, params);
		while(resultSet.next()){
			String id = resultSet.getString("id");
			String name = resultSet.getString("name");
			double lat = resultSet.getDouble("lat");
			double lng = resultSet.getDouble("lng");
			String summary = resultSet.getString("summary");
			String imgSrc = resultSet.getString("imgSrc");
			String address = resultSet.getString("address");
			String otaname = resultSet.getString("otaname");
			double price = resultSet.getDouble("price");
			String bookUrl = resultSet.getString("bookUrl");
			String cityId = resultSet.getString("cityId");
			String cityName = resultSet.getString("cityName");
			
			MfwHotel hotel = new MfwHotel(id, name, lat, lng, summary, imgSrc, address, otaname, price, bookUrl, cityId, cityName);
			hotelList.add(hotel);
		}
		System.out.println();
	}
	
	/**
	 * 从数据库中选择城市的景点
	 * @param pCityName 城市的名称，如：广州
	 * @throws SQLException
	 */
	private void selectScenery(String pCityName) throws SQLException{
		String sql = "SELECT * FROM t_scenery as s WHERE s.parent_sid=(SELECT sid FROM t_scenery WHERE t_scenery.sname=?)";
		String[] params = {pCityName};
		ResultSet resultSet = DbUtil.executeQuery(sql, params);
		while(resultSet.next()){
			String sid = resultSet.getString("sid");
			String surl = resultSet.getString("surl");
			String sname = resultSet.getString("sname");
			String ambiguitySname = resultSet.getString("ambiguity_sname");
			String parentSid = resultSet.getString("parent_sid");
			int viewCount = resultSet.getInt("view_count");
			String mapInfo = resultSet.getString("map_info");
			String mapArr[] = mapInfo.split(",");
			double lng = Double.parseDouble(mapArr[0]);
			double lat = Double.parseDouble(mapArr[1]);
			
			Scenery scenery = new Scenery();
			scenery.setSid(sid);
			scenery.setSurl(surl);
			scenery.setSname(sname);
			scenery.setAmbiguitySname(ambiguitySname);
			scenery.setParentSid(parentSid);
			scenery.setViewCount(viewCount);
			scenery.setMapX(lat);
			scenery.setMapY(lng);
			sceneryList.add(scenery);
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		SceneryHotelLinker linker = new SceneryHotelLinker();
	}
}
