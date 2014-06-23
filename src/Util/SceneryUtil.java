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
	 * ��recommend_visit_time�ֶ��н��������������
	 * @param dayStr
	 * @return
	 */
	public static double getVisitDays(String dayStr){
		
		double dayHours = 8.0;
		if (dayStr == null || dayStr.equals("")) {
			return 0.5;
		}
		if (dayStr.contains("��Сʱ")) {
			return 2 / dayHours;
		}
		if (dayStr.contains("����")) {
			return 0.5;
		}
		if (dayStr.contains("����")) {
			return 0.5;
		}
		if (dayStr.contains("һ��")) {
			return 1.0;
		}
		if (dayStr.contains("����")) {
			return 2.0;
		}
		if (dayStr.contains("����")) {
			return 3.0;
		}
		if (dayStr.contains("����")) {
			return 4.0;
		}
		
		//ƥ��Сʱ
		Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)-?(\\d+(\\.\\d+)?)?(\\S*?)Сʱ");
		Matcher matcher = pattern.matcher(dayStr);
		if(matcher.find()){
			String result = matcher.group(1);
//			System.out.println(result);
			return Double.parseDouble(result)*2 / dayHours;
		}
		
		//ƥ�����
		pattern = Pattern.compile("(\\d+(\\.\\d+)?)-?(\\d+(\\.\\d+)?)?(\\S*?)����");
		matcher = pattern.matcher(dayStr);
		if (matcher.find()) {
			String result = matcher.group(1);
			double day = Double.parseDouble(result)*4 / (60.0 * dayHours);
//			System.out.println(result + "-" + day);
			return day;
		}
		
		//ƥ����
		pattern = Pattern.compile("(\\d+(\\.\\d+)?)-?(\\d+(\\.\\d+)?)?(\\S*?)��");
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
	 * �������������Ʊ
	 * @param priceStr
	 * @return
	 */
	public static double parsePrice(String priceStr){
		double price = 0.0;
		if (priceStr == null || priceStr.equals("")) {
			return price;
		}
//		priceStr = "1. ����Ʊ��102.00�۱�2. ��Ʊ���������Դ塢¡����Ժ���ٲ���̬԰����150.00Ԫ";
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+)(Ԫ|�۱�|��̨��)");
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
	 * ��øó����µ����о���
	 * @param cityId ���е�sid
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
//						System.err.println(sname+":�������");
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
	 * ��øó����µ����о���
	 * @param cityId ���е�sid
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
	 * ���һ�����ж���
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
	 * �����������浽�ļ�����
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
			String filename = i +"_" + route.getUid() + ".txt";
			AppUtil.exportFile(dirPath + "\\" + filename, rootObj.toString());
//			System.out.println(rootObj.toString());
					
		}
	}
	/**
	 * �������·�̵İ��ţ�������·���з�Ϊ�������
	 * @param sceneList
	 * @param upDay ������
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
				daysObj.put("curDay", "��" + curDay + "��");
				allDaysArr.add(daysObj);
				
				tmpList.clear();
				curDay ++;
				//һ���治�꣬�ڶ��������
				if (tmpDays >= 0.3) {
					tmpList.add(scenery);
				}
			}
		}
		return allDaysArr;
	}
	
	public static void main(String[] args){
//		getSceneryList("da666bc57594baeb76b3bcf0");
		getSceneryList("622bc401f1153f0fd41f74dd");
//		getSceneryMap("622bc401f1153f0fd41f74dd");
//		getCity("da666bc57594baeb76b3bcf0");
//		parsePrice("");
	}

}
