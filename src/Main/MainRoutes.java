package Main;

import java.util.ArrayList;
import java.util.HashMap;

import Model.Hotel;
import Model.Route;
import Model.Scenery;
import Util.HotelUtil;
import Util.SceneryUtil;

/**
 * ��������·��
 * @author yinchuandong
 *
 */
public class MainRoutes {

	public void caluate(String cityId, double downDay, double upDay){
		long begin = System.currentTimeMillis();
		HashMap<String, Hotel> hotelMap = HotelUtil.getAllHotel();
		
		GaScenery ga = new GaScenery(300, 1000, 0.8, 0.9);
		ga.init(cityId, downDay, upDay, hotelMap);
		
		ArrayList<Route> routeList = ga.solve();
		
		for (Route route : routeList) {
			GaSort gaSort = new GaSort(30, 100, 0.8, 0.9);
			gaSort.init(route.getSceneryList());
			ArrayList<Scenery> sceneList = gaSort.solve();
			route.setSceneryList(sceneList);//���ź���Ķ������¼���route��
			route.setDistance(gaSort.getBestLen());//��¼��̵�·��ֵ
			for (Scenery scenery : sceneList) {
				System.out.print(scenery.getSname() + ",");
			}
			System.out.println("--�ȶȣ�" + route.getHotness() + "--�۸�"+route.getSumPrice() + "--���ȣ�"+gaSort.getBestLen());
		}
		SceneryUtil.saveRoutes(routeList, "E:\\traveldata\\routes\\" + routeList.get(0).getSurl());
		
		System.out.println("�ܹ���" + routeList.size() +"��·��");
		long end = System.currentTimeMillis();
		long time = (end - begin);
		System.out.println();
		System.out.println("��ʱ��"+ time +" ms");
	}
	
	public static void main(String[] args){
		
	}
}
