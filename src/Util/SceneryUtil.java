package Util;

import java.io.BufferedReader;
import java.io.File;
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
		
		double dayHours = 8.0;
		if (dayStr == null || dayStr.equals("")) {
			return 0.5;
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
		
		return 0.5;
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
				String sql = "SELECT s.sid,s.surl,s.sname,s.scene_layer,s.view_count,s.map_x,s.map_y,s.price_desc,s.recommend_visit_time FROM t_scenery as s WHERE s.parent_sid=?";
				String[] params = {waitList.poll()};
				ResultSet set = DbUtil.executeQuery(sql, params);
				while(set.next()){
					int sceneLayer = set.getInt("scene_layer");
					String sid = set.getString("sid");
					String surl = set.getString("surl");
					String sname = set.getString("sname");
					int viewCount = set.getInt("view_count");
					double mapX = set.getDouble("map_x");
					double mapY = set.getDouble("map_y");
					double price = parsePrice(set.getString("price_desc"));
					double visitDay = getVisitDays(set.getString("recommend_visit_time"));
					
					if (sceneLayer == 6) {
						Scenery scenery = new Scenery();
						scenery.setSid(sid);
						scenery.setSurl(surl);
						scenery.setSname(sname);
						scenery.setViewCount(viewCount);
						scenery.setMapX(mapX);
						scenery.setMapY(mapY);
						scenery.setPrice(price);
						scenery.setVisitDay(visitDay);
						System.out.println(sname+":"+price);
						result.put(sid, scenery);
					}else{
						waitList.offer(sid);
						System.err.println(sname+":加入队列");
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
	
	
	public static void main(String[] args){
//		getCityScenery("da666bc57594baeb76b3bcf0");
		getSceneryMap("622bc401f1153f0fd41f74dd");
//		parsePrice("");
	}

}
