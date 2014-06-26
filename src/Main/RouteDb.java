package Main;

import java.io.File;

import net.sf.json.JSONObject;

import Model.Route;
import Util.AppUtil;
import Util.DbUtil;

public class RouteDb {
	
	/**
	 * 将路线保存到数据库
	 * @param model
	 * @param jsonName
	 * @param routeDesc
	 */
	public static void save(Route model, String jsonName, String routeDesc){
		String sql = "insert into t_route (uid,sid,sname,ambiguity_sname,hotness,view_count,sum_price,hotel_price,scene_ticket,visit_day,down_day,up_day,distance,json_name,route_desc) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String[] params = {
				model.getUid(),
				model.getSid(),
				model.getSurl(),
				model.getSname(),
				model.getAmbiguitySname(),
				model.getHotness()+"",
				model.getViewCount()+"",
				model.getSumPrice()+"",
				model.getHotelPrice()+"",
				model.getSceneTicket()+"",
				model.getVisitDay()+"",
				model.getDownDay()+"",
				model.getUpDay()+"",
				model.getDistance()+"",
				jsonName,
				routeDesc
		};
		
		DbUtil.executeUpdate(sql, params);
	}
	
	public static void run(){
		File file = new File("E:\\traveldata\\routes3\\aba\\3_0_fec47b2ba8e87c16a9e39a048936c.json");
		String content = AppUtil.readFile(file);
		JSONObject rootObj = JSONObject.fromObject(content);
		String uid = rootObj.getString("");
	}
	
	public static void main(String[] args){
		
	}

}
