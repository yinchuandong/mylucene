package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import Model.Scenery;

/**
 * ̰���㷨�����һ����ռ�
 * @author yinchuandong
 *
 */
public class GreedyUtil {

	/**
	 * ���е��б�
	 */
	private ArrayList<Scenery> sceneryList;
	
	/**
	 * ���յ�·���б�
	 */
	private HashMap<String, Routes> routesMap;
	/**
	 * ��������
	 */
	private double upDay = 3.0;
	/**
	 * ��������
	 */
	private double downDay = 2.0;
	
	private int scale = 0;
	
	private int[][] initPopulation;
	
	
	public GreedyUtil(double downDay, double upDay, int scale, ArrayList<Scenery> sceneryList){
		this.sceneryList = sceneryList;
		this.scale = scale;
		routesMap = new HashMap<>();
		initPopulation = new int[scale][sceneryList.size()];
		
		this.init();
	}
	
	private void init(){
		this.calculate();
		this.code();
	}
	
	/**
	 * ��������������ĵ�һ��
	 */
	private void calculate(){
		int len = sceneryList.size();
		//���ƿ��
		for (int span = 1; span < len; span++) {
			for(int i=0; i < len; i++){
				double tmpDays = 0.0;
				int tmpViewCount = 0;
				String tmpRoute = "";
				//һ��·����ǰһ������
				Scenery lastScene = sceneryList.get(0);
				double distance = 0.0;
				for (int j = i; j < len; j += span) {
					Scenery curScene = sceneryList.get(j);
					tmpDays += curScene.getVisitDay();
					distance = getDistance(lastScene, curScene);
					if (tmpDays > upDay || distance > 200000.0) {
						tmpDays -= curScene.getVisitDay();
						continue;
					}
					tmpRoute += j + ",";
					tmpViewCount += curScene.getViewCount();
					if (tmpDays > downDay && tmpDays <= upDay) {
						tmpRoute = tmpRoute.substring(0, tmpRoute.length() - 1);
						Routes routes = new Routes();
						routes.setRoute(tmpRoute);
						routes.setDays(tmpDays);
						routes.setViewCount(tmpViewCount);
						String key = AppUtil.md5(tmpRoute);
						if (!routesMap.containsKey(key)) {
							routesMap.put(key, routes);
						}
						tmpRoute = "";
						tmpDays = 0;
						tmpViewCount = 0;
					}
				}
			}
		}
	}
	
	
	
	/**
	 * ��������Ľ�����ļ���
	 * ����Ϊ 0,1,0,1,1�������ͣ�1������±��Ӧ�ĳ��б�ѡ��,0������ѡ��
	 */
	private void code(){
		Iterator<String> iter = routesMap.keySet().iterator();
		int k = 0;
		ArrayList<Routes> routeList = new ArrayList<Routes>();
		while(iter.hasNext()){
			String key = (String) iter.next();
			Routes model = routesMap.get(key);
			routeList.add(model);
		}
		Collections.sort(routeList);
		ArrayList<Routes> sortedList = new ArrayList<>();
		if (routeList.size() > scale) {
			for (int i = 0; i < scale; i++) {
				sortedList.add(routeList.get(i));
			}
		}else{
			int len = routeList.size();
			if (len != 0) {
				for (int i = 0; i < scale; i++) {
					sortedList.add(routeList.get(i % len));
				}
			}
		}
		
		for (Routes model : sortedList) {
			String routes = model.getRoute();
			String[] arr = routes.split(",");
			
			String codeStr = "";
			for (int i = 0; i < sceneryList.size(); i++) {
				boolean isFound = false;
				for (int j = 0; j < arr.length; j++) {
					if (i == Integer.parseInt(arr[j])) {
						isFound = true;
						break;
					}else{
						isFound = false;
					}
				}
				if (isFound) {
					codeStr += "1,";
					initPopulation[k][i] = 1;
				}else{
					codeStr += "0,";
					initPopulation[k][i] = 0;
				}
			}
			k++;
//			codeStr = codeStr.substring(0, codeStr.length() - 1) + "\r\n";
			System.out.print(codeStr);
		}
		
		
	}
	
	/**
	 * ����ŷʽ����
	 * @param s1
	 * @param s2
	 * @return
	 */
	private double getDistance(Scenery s1, Scenery s2){
		double distance = 0.0;
		double x1 = s1.getMapX();
		double y1 = s1.getMapY();
		double x2 = s2.getMapX();
		double y2 = s2.getMapY();
		distance = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
		return distance;
	}
	
	/**
	 * ��ó�ʼ���õ���Ⱥ
	 * @return
	 */
	public int[][] getInitPopulation() {
		return initPopulation;
	}
	
	public class Routes implements Comparable<Routes>{
		private String route;
		private int viewCount;
		private double days;
		public String getRoute() {
			return route;
		}
		public void setRoute(String route) {
			this.route = route;
		}
		public int getViewCount() {
			return viewCount;
		}
		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}
		public double getDays() {
			return days;
		}
		public void setDays(double days) {
			this.days = days;
		}
		@Override
		public int compareTo(Routes o) {
			if (this.viewCount > o.getViewCount()) {
				return -1;
			}else{
				return 1;
			}
		}
		
	}
	
	public static void test(){
		double x1 = 12616984.063376;
		double y1 = 2616239.6646102;
		double x2 = 12622859.013427;
		double y2 = 2608180.9771793;
		double distance = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
		System.out.println(distance);
	}

	public static void main(String[] args) throws IOException{
		ArrayList<Scenery> sceneryList = SceneryUtil.getSceneryList("622bc401f1153f0fd41f74dd");
//		ArrayList<Scenery> sceneryList = SceneryUtil.getSceneryList("da666bc57594baeb76b3bcf0");
		GreedyUtil model = new GreedyUtil(2.0, 3.0, 300, sceneryList);
		model.getInitPopulation();
//		test();
		System.out.println();
	}
}
