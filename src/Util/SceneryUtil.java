package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import Model.Route;
import Model.Scenery;

public class SceneryUtil {
	
	
	public static void parse() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File("./time_map.txt")));
		PrintWriter writer = new PrintWriter(new File("./time_map_days.txt"));
		String buff = null;
		while((buff = reader.readLine()) != null){
			double days = getVisitDays(buff);
			String str = buff + " " + days;
			writer.write(str + "\r\n");
		}
		
		writer.close();
		reader.close();
	}
	
	/**
	 * 从recommend_visit_time字段中解析出游玩的天数
	 * @param dayStr
	 * @return
	 */
	public static double getVisitDays(String dayStr){
		
		double dayHours = 10.0;
		if (dayStr == null || dayStr.equals("")) {
			return 0.3;
		}
		if (dayStr.contains("半小时")) {
			return 2 / dayHours;
		}
		if (dayStr.contains("晚上")) {
			return 0.5;
		}
		if (dayStr.contains("半天")) {
			return 0.5;
		}
		if (dayStr.contains("一天")) {
			return 1.0;
		}
		if (dayStr.contains("两天")) {
			return 2.0;
		}
		if (dayStr.contains("三天")) {
			return 3.0;
		}
		if (dayStr.contains("四天")) {
			return 4.0;
		}
		
		//匹配小时
		Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)-?(\\d+(\\.\\d+)?)?(\\S*?)小时");
		Matcher matcher = pattern.matcher(dayStr);
		if(matcher.find()){
			String result = matcher.group(1);
//			System.out.println(result);
			return Double.parseDouble(result)*2 / dayHours;
		}
		
		//匹配分钟
		pattern = Pattern.compile("(\\d+(\\.\\d+)?)-?(\\d+(\\.\\d+)?)?(\\S*?)分钟");
		matcher = pattern.matcher(dayStr);
		if (matcher.find()) {
			String result = matcher.group(1);
			double day = Double.parseDouble(result)*4 / (60.0 * dayHours);
//			System.out.println(result + "-" + day);
			return day;
		}
		
		//匹配天
		pattern = Pattern.compile("(\\d+(\\.\\d+)?)-?(\\d+(\\.\\d+)?)?(\\S*?)天");
		matcher = pattern.matcher(dayStr);
		if (matcher.find()) {
			String result = matcher.group(1);
			double day = Double.parseDouble(result);
//			System.out.println(result);
			return day;
		}
		
		return 0.3;
	}
	
	
	/**
	 * 解析出景点的门票
	 * @param priceStr
	 * @return
	 */
	public static double parsePrice(String priceStr){
		double price = 0.0;
		if (priceStr == null || priceStr.equals("")) {
			return price;
		}
//		priceStr = "1. 单人票：102.00港币2. 联票（含诸葛八卦村、隆丰禅院、百草生态园）：150.00元";
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+)(元|港币|新台币)");
		Matcher matcher = pattern.matcher(priceStr);
		if (matcher.find()) {
			String result = matcher.group(1);
			price = Double.parseDouble(result);
			return price;
		}
		try {
			price = Double.parseDouble(priceStr);
		} catch (Exception e) {
		}
		return price;
	}
	
	/**
	 * 获得该城市下的所有景点
	 * @param cityId 城市的sid
	 * @return
	 */
	public static HashMap<String, Scenery> getSceneryMap(String cityId){
		HashMap<String, Scenery> result = new HashMap<String, Scenery>();
		
		LinkedList<String> waitList = new LinkedList<String>();
		waitList.add(cityId);
		try {
			while(!waitList.isEmpty()){
				String sql = "SELECT s.sid,s.surl,s.sname,s.ambiguity_sname,s.scene_layer,s.view_count,s.lat,s.lng,s.map_x,s.map_y,s.price_desc,s.recommend_visit_time,s.more_desc,s.full_url FROM t_scenery as s WHERE s.parent_sid=?";
				String[] params = {waitList.poll()};
				ResultSet set = DbUtil.executeQuery(sql, params);
				while(set.next()){
					int sceneLayer = set.getInt("scene_layer");
					String sid = set.getString("sid");
					String surl = set.getString("surl");
					String sname = set.getString("sname");
					String ambiguitySname = set.getString("ambiguity_sname");
					String moreDesc = set.getString("more_desc");
					String fullUrl = set.getString("full_url");
					int viewCount = set.getInt("view_count");
					double lng = set.getDouble("lng");
					double lat = set.getDouble("lat");
					double mapX = set.getDouble("map_x");
					double mapY = set.getDouble("map_y");
					double price = parsePrice(set.getString("price_desc"));
					double visitDay = getVisitDays(set.getString("recommend_visit_time"));
					
					if (sceneLayer == 6) {
						Scenery scenery = new Scenery();
						scenery.setSid(sid);
						scenery.setSurl(surl);
						scenery.setSname(sname);
						scenery.setAmbiguitySname(ambiguitySname);
						scenery.setMoreDesc(moreDesc);
						scenery.setFullUrl(fullUrl);
						scenery.setViewCount(viewCount);
						scenery.setLng(lng);
						scenery.setLat(lat);
						scenery.setMapX(mapX);
						scenery.setMapY(mapY);
						scenery.setPrice(price);
						scenery.setVisitDay(visitDay);
//						System.out.println(sname+":"+price);
						result.put(sid, scenery);
					}else{
						waitList.offer(sid);
//						System.err.println(sname+":加入队列");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DbUtil.close();
		}
		
		return result;
	}
	
	/**
	 * 获得该城市下的所有景点
	 * @param cityId 城市的sid
	 * @return
	 */
	public static ArrayList<Scenery> getSceneryList(String cityId){
		HashMap<String, Scenery> sceneryMap = SceneryUtil.getSceneryMap(cityId);
		Iterator<String> iter = sceneryMap.keySet().iterator();
		ArrayList<Scenery> sceneryList = new ArrayList<Scenery>();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			sceneryList.add(sceneryMap.get(key));
		}
		return sceneryList;
	}
	
	/**
	 * 获得一个城市对象
	 * @param psid
	 * @return
	 */
	public static Scenery getCity(String psid){
		Scenery scenery = new Scenery();
		try {
			String sql = "SELECT s.sid,s.surl,s.sname,s.ambiguity_sname,s.scene_layer,s.view_count,s.lat,s.lng,s.map_x,s.map_y FROM t_scenery as s WHERE s.sid=?";
			String[] params = {psid};
			ResultSet set = DbUtil.executeQuery(sql, params);
			while(set.next()){
				String sid = set.getString("sid");
				String surl = set.getString("surl");
				String sname = set.getString("sname");
				String ambiguitySname = set.getString("ambiguity_sname");
				int viewCount = set.getInt("view_count");
				double lng = set.getDouble("lng");
				double lat = set.getDouble("lat");
				double mapX = set.getDouble("map_x");
				double mapY = set.getDouble("map_y");
				
				scenery.setSid(sid);
				scenery.setSurl(surl);
				scenery.setSname(sname);
				scenery.setAmbiguitySname(ambiguitySname);
				scenery.setViewCount(viewCount);
				scenery.setLng(lng);
				scenery.setLat(lat);
				scenery.setMapX(mapX);
				scenery.setMapY(mapY);
			}
		} catch (Exception e) {
			scenery = null;
			e.printStackTrace();
		} finally{
			DbUtil.close();
		}
		return scenery;
	}
	
	/**
	 * 计算欧式距离
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static double calcDistance(Scenery s1, Scenery s2){
		double distance = 0.0;
		double x1 = s1.getMapX();
		double y1 = s1.getMapY();
		double x2 = s2.getMapX();
		double y2 = s2.getMapY();
		distance = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
		return distance;
	}
	
	/**
	 * 将计算结果保存到文件夹中
	 * @param routeList
	 * @param dirPath
	 */
	public static void saveRoutes(ArrayList<Route> routeList, String dirPath){
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (int i = 0; i < routeList.size(); i++) {
			Route route  = routeList.get(i);
			JSONObject rootObj = JSONObject.fromObject(route);
			//
			rootObj.put("arrange", arrangeRoute(route.getSceneryList(), route.getUpDay()));
			JSONArray sceneArr = rootObj.getJSONArray("sceneryList");
			for (int j = 0; j < sceneArr.size(); j++) {
				JSONObject sceneObj = sceneArr.getJSONObject(j);
				sceneObj.put("recommendHotel", HotelUtil.getSceneHotel(sceneObj.getString("sid")));
			}
			String filename = (int)route.getUpDay() + "_" + i +"_" + route.getUid() + ".json";
			AppUtil.exportFile(dirPath + "\\" + filename, rootObj.toString());
//			System.out.println(rootObj.toString());
					
		}
	}
	/**
	 * 获得整个路程的安排，将整个路程切分为按天计算
	 * @param sceneList
	 * @param upDay 几天游
	 */
	private static JSONArray arrangeRoute(ArrayList<Scenery> sceneList, double upDay){
		JSONArray allDaysArr = JSONArray.fromObject("[]");
		double tmpDays = 0.0;
		int curDay = 1;
		ArrayList<Scenery> tmpList = new ArrayList<Scenery>();
		for (Scenery scenery : sceneList) {
			tmpDays += scenery.getVisitDay();
			tmpList.add(scenery);
			if (tmpDays >= 1.0 || upDay <= 1.0) {
				tmpDays -= 1.0;
				JSONObject daysObj = JSONObject.fromObject("{}");
				JSONArray daysArr = JSONArray.fromObject(tmpList);
				daysObj.put("list", daysArr);
				daysObj.put("hotel", HotelUtil.getSceneHotel(scenery.getSid()));
				daysObj.put("curDay", "第" + curDay + "天");
				allDaysArr.add(daysObj);
				
				tmpList.clear();
				curDay ++;
				//一天玩不完，第二天继续玩
				if (tmpDays >= 0.3) {
					tmpList.add(scenery);
				}
			}
		}
		return allDaysArr;
	}
	
	/**
	 * 将景点导入到text中
	 */
	public static void exportToText(){
		try {
			PrintWriter writer = new PrintWriter(new File("/Users/yinchuandong/Documents/guangzhou.txt"));
			ArrayList<Scenery> list = getSceneryList("da666bc57594baeb76b3bcf0");
			for (Scenery scenery : list) {
				String str = "";
				str += scenery.getSname() + ",";
				str += scenery.getViewCount() + ",";
				str += scenery.getVisitDay();
				writer.println(str);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
//		getSceneryList("da666bc57594baeb76b3bcf0");
		
//		exportToText();
		
//		getSceneryList("622bc401f1153f0fd41f74dd");
//		getSceneryMap("622bc401f1153f0fd41f74dd");
//		getCity("da666bc57594baeb76b3bcf0");
//		parsePrice("");
		System.out.println("end");
	}

}
