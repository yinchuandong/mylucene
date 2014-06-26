package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Model.Hotel;
import Model.Route;
import Model.Scenery;
import Util.HotelUtil;
import Util.SceneryUtil;

/**
 * 批量运算路径
 * @author yinchuandong
 *
 */
public class MainRoutes {

	public static void caluate(String cityId, double downDay, double upDay) throws Exception{
		long begin = System.currentTimeMillis();
		HashMap<String, Hotel> hotelMap = HotelUtil.getAllHotel();
		
		GaScenery ga = new GaScenery(300, 1000, 0.8, 0.9);
		ga.init(cityId, downDay, upDay, hotelMap);
		
		ArrayList<Route> routeList = ga.solve();
		
		for (Route route : routeList) {
			try {
				GaSort gaSort = new GaSort(30, 100, 0.8, 0.9);
				gaSort.init(route.getSceneryList());
				ArrayList<Scenery> sceneList = gaSort.solve();
				route.setSceneryList(sceneList);//将排好序的对象重新加入route中
				route.setDistance(gaSort.getBestLen());//记录最短的路径值
				for (Scenery scenery : sceneList) {
					System.out.print(scenery.getSname() + ",");
				}
				System.out.println("--热度：" + route.getHotness() + "--价格："+route.getSumPrice() + "--长度："+gaSort.getBestLen());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SceneryUtil.saveRoutes(routeList, "E:\\traveldata\\routes" + (int)upDay +"\\" + routeList.get(0).getSurl());
		
		System.out.println("总共：" + routeList.size() +"条路径");
		long end = System.currentTimeMillis();
		long time = (end - begin);
		System.out.println();
		System.out.println("耗时："+ time +" ms");
	}
	
	private static void parseRoute(double downDay, double upDay) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File("./city_id.txt")));
		String buff = null;
		int i = 0;
		while((buff = reader.readLine()) != null){
			try {
				caluate(buff, downDay, upDay);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("处理完第：" + (i++) +"个城市-->");
		}
		reader.close();
	}
	
	public static void main(String[] args){
		try {
			long begin = System.currentTimeMillis();
			parseRoute(1.0, 2.0);
//			parseRoute(3.0, 4.0);
//			parseRoute(4.0, 5.0);
			long end = System.currentTimeMillis();
			long time = (end - begin);
			System.out.println();
			System.out.println("耗时："+ time +" ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
