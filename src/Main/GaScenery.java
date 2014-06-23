package Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.rmi.CORBA.Tie;
import javax.xml.crypto.Data;

import org.omg.PortableInterceptor.HOLDING;

import Model.Route;
import Model.Scenery;
import Model.Hotel;
import Util.AppUtil;
import Util.DbUtil;
import Util.GreedyUtil;
import Util.HotelUtil;
import Util.SceneryUtil;

public class GaScenery {
	
	/**
	 * ��Ⱥ��ģ
	 */
	private int scale;
	
	/**
	 * ��������
	 */
	private int sceneryNum;
	
	/**
	 * �����б�
	 */
	private ArrayList<Scenery> sceneryList;
	
	/**
	 * ������д���
	 */
	private int maxGen;
	
	/**
	 * ��ǰ���д���
	 */
	private int curGen;
	
	/**
	 * �������
	 */
	private double pc;
	
	/**
	 * �������
	 */
	private double pm;
	
	/**
	 * ��Ⱥ�и�����ۼƸ���
	 */
	private double[] pi;
	
	/**
	 *  ��ʼ��Ⱥ��������Ⱥ��������ʾ��Ⱥ��ģ��һ�д���һ�����壬��Ⱦɫ�壬�б�ʾȾɫ�����Ƭ��
	 */
	private int[][] oldPopulation;
	
	/**
	 * �µ���Ⱥ���Ӵ���Ⱥ
	 */
	private int[][] newPopulation;
	
	/**
	 * ��Ⱥ��Ӧ�ȣ���ʾ��Ⱥ�и����������Ӧ��
	 */
	private double[] fitness;
	
	/**
	 * ÿһ��·����Ӧ�ľƵ��sid
	 */
	private String[] recommendHotel;
	
	/**
	 * ��ѳ��ִ���
	 */
	private int bestGen;
	
	/**
	 * ��ѳ���
	 */
	private double bestLen;
	
	/**
	 * ���·��
	 */
	private int[] bestRoute;
	
	private String bestHotelIds;
	
	/**
	 * �����
	 */
	private Random random;
	
	/**
	 * �Ƶ��б�
	 */
	private HashMap<String, Hotel> hotelMap;
	
	/**
	 * ��������������
	 */
	private double upDay = 3;
	/**
	 * ��������������
	 */
	private double downDay = 2.0;
	
	/**
	 * �������ڵĳ��е�id
	 */
	private String cityId;
	
	/**
	 * 
	 * @param scale ��Ⱥ��ģ
	 * @param maxGen ���д���
	 * @param pc �������
	 * @param pm �������
	 */
	public GaScenery(int scale, int maxGen, double pc, double pm){
		this.scale = scale;
		this.maxGen = maxGen;
		this.pc = pc;
		this.pm = pm;
		this.sceneryList = new ArrayList<Scenery>();
	}
	

	/**
	 * ����һ��0-65535֮��������
	 * @return
	 */
	private int getRandomNum(){
		return this.random.nextInt(65535);
	}
	
	/**
	 * ��ʼ���㷨����file�м��������ļ�
	 * @param cityId ���е�sid
	 * @param downDay �������ޣ�������
	 * @param upDay �������ޣ�������
	 * @param hotelMap �Ƶ����Ϣ
	 * @throws IOException 
	 */
	public void init(String cityId, double downDay, double upDay, HashMap<String, Hotel> hotelMap) throws IOException{
		this.cityId = cityId;
		this.downDay = downDay;
		this.upDay = upDay;
		this.hotelMap = hotelMap;
		
		this.sceneryList = SceneryUtil.getSceneryList(cityId);
		
		this.sceneryNum = this.sceneryList.size();
		
		this.bestLen = Integer.MIN_VALUE;
		this.bestGen = 0;
		this.bestRoute = new int[sceneryNum];
		this.bestHotelIds = "";
		this.curGen = 0;
		
		this.newPopulation = new int[scale][sceneryNum];
		this.oldPopulation = new int[scale][sceneryNum];
		this.fitness = new double[scale];
		this.pi = new double[scale];
		
		this.recommendHotel = new String[scale];
		
		this.random = new Random(System.currentTimeMillis());
	}
	
	/**
	 * ��ʼ����Ⱥ
	 * ��01001����ʽ����Ⱦɫ��
	 */
	private void initGroup(){
		GreedyUtil util = new GreedyUtil(downDay, upDay, scale, sceneryList);
		oldPopulation = util.getInitPopulation();
	}
	
	/**
	 * ���ۺ��������ڼ����ʶ�
	 * @param index ��ǰȾɫ����±�
	 * @param chromosome Ⱦɫ�壬����������1,����2...����n
	 * @return the total distance of all chromosome's cities;
	 */
	private double evaluate(int index, int[] chromosome){
		double ticketPrice = 0;//��Ʊ
		double hotness = 0;//�ȶ�
		double days = 0.0;
		//�Ƶ굱ǰȾɫ���Ӧ�ľƵ���Ϣ
		ArrayList<Hotel> hotels = new ArrayList<Hotel>();
		for (int i = 0; i < chromosome.length; i++) {
			if (chromosome[i] == 1) {
				Scenery scene = sceneryList.get(i);
				ticketPrice +=  scene.getPrice();
				hotness += (double)scene.getViewCount();
				days += scene.getVisitDay();
				//��øþ���ľƵ���Ϣ
				Hotel hotel = hotelMap.get(scene.getSid());
				if (hotel != null) {
					hotels.add(hotel);
				}
			}
		}
		
		if (days <= downDay || days > upDay) {
			recommendHotel[index] = "";
			return 0.0000000000000000001;
		}
		
		Collections.sort(hotels);
		double hotelPrice = 0.0;
		/* �жϾƵ�ĸ����Ƿ������Ҫ��ס������
		 * �������������ס����������۸�
		 * ���С����������оƵ�ļ۸�ʣ�������Ͱ�����ͼ۸����
		 */
		String hotelIds = "";//�����Ƽ���hotelId
		int len = Math.min(hotels.size(), (int)downDay);
		if (len != 0) {
			for (int i = 0; i < len; i++) {
				hotelPrice += hotels.get(i).getPrice();
				hotelIds += hotels.get(i).getSid() + ",";
			}
			int span = (int)(downDay - hotels.size());
			for (int i = 0; i < span; i++) {
				hotelPrice += hotels.get(0).getPrice();
				hotelIds += hotels.get(i).getSid() + ",";
			}
		}
		if (!hotelIds.equals("")) {
			hotelIds = hotelIds.substring(0, hotelIds.length() - 1);
		}
		recommendHotel[index] = hotelIds;
		
		double price = hotelPrice + ticketPrice;
		double fitness = (10000.0 / (price + 10.0)) * 0.1 + Math.pow(hotness, 1.0/3.0) * 0.9;
//		System.out.println("fiteness:" + fitness);
		return fitness;
	}
	
	/**
	 * ������Ⱥ�и���������ۻ����ʣ�
	 * ǰ�����Ѿ�����������������Ӧ��fitness[max]��
	 * ��Ϊ����ѡ�����һ���֣�Pi[max]
	 */
	private void countRate(){
		double sumFitness = 0; 
		for (int i = 0; i < scale; i++) {
			sumFitness += fitness[i];
		}
		
		//�����ۼƸ���
		this.pi[0] = fitness[0] / sumFitness;
		for (int i = 1; i < scale; i++) {
			pi[i] = (fitness[i] / sumFitness) + pi[i - 1]; 
		}
	}
	
	/**
	 *  ��ѡĳ����Ⱥ����Ӧ����ߵĸ��壬ֱ�Ӹ��Ƶ��Ӵ��У�
	 *  ǰ�����Ѿ�����������������Ӧ��Fitness[max]
	 */
	private void selectBestGh(){
		int maxId = 0;
		double maxEvaluation = fitness[0];
		//��¼�ʶ�����cityId���ʶ�
		for (int i = 1; i < scale; i++) {
			if (maxEvaluation < fitness[i]) {
				maxEvaluation = fitness[i];
				maxId = i;
			}
		}
		
		//��¼��õ�Ⱦɫ����ִ���
		if (bestLen < maxEvaluation) {
			bestLen = maxEvaluation;
			bestGen = curGen;
			for (int i = 0; i < sceneryNum; i++) {
				bestRoute[i] = oldPopulation[maxId][i];
			}
		}
		
		//��¼��þ����Ӧ�ľƵ�
		bestHotelIds = recommendHotel[maxId];
		
		// ��������Ⱥ����Ӧ����ߵ�Ⱦɫ��maxId���Ƶ�����Ⱥ�У����ڵ�һλ0
		this.copyGh(0, maxId);
	}
	
	/**
	 * ����Ⱦɫ�壬��oldPopulation���Ƶ�newPopulation
	 * @param curP ��Ⱦɫ������Ⱥ�е�λ��
	 * @param oldP �ɵ�Ⱦɫ������Ⱥ�е�λ��
	 */
	private void copyGh(int curP, int oldP){
		for (int i = 0; i < sceneryNum; i++) {
			newPopulation[curP][i] = oldPopulation[oldP][i];
		}
	}
	
	/**
	 * ����ѡ�������ѡ
	 */
	private void select(){
		int selectId = 0;
		double tmpRan;
//		System.out.print("selectId:");
		for (int i = 1; i < scale; i++) {
			tmpRan = (double)((getRandomNum() % 1000) / 1000.0);
			for (int j = 0; j < scale; j++) {
				selectId = j;
				if (tmpRan <= pi[j]) {
					break;
				}
			}
//			System.out.print(selectId+" ");
			copyGh(i, selectId);
		}
	}
	
	/**
	 * ���������������������
	 */
	public void evolution(){
		// ��ѡĳ����Ⱥ����Ӧ����ߵĸ���
		selectBestGh();
		// ����ѡ�������ѡscale-1����һ������
		select();
		
		double ran;
		for (int i = 0; i < scale; i = i+2) {
			ran = random.nextDouble();
			if (ran < this.pc) {
				//���С��pc������н���
				crossover(i, i+1);
			}else{
				//���ߣ����б���
				ran = random.nextDouble();
				if (ran < this.pm) {
					//����Ⱦɫ��i
					onVariation(i);
				}
				
				ran = random.nextDouble();
				if (ran < this.pm) {
					//����Ⱦɫ��i+1
					onVariation(i + 1);
				}
			}
		}
	}
	
	/**
	 * ���㽻��,��ͬȾɫ�彻�������ͬ�Ӵ�Ⱦɫ��
	 * @param k1 Ⱦɫ���� 1|234|56
	 * @param k2 Ⱦɫ���� 7|890|34
	 */
	private void crossover(int k1, int k2){
		//������������λ��
		int pos1 = getRandomNum() % sceneryNum;
		int pos2 = getRandomNum() % sceneryNum;
		//ȷ��pos1��pos2����λ�ò�ͬ
		while(pos1 == pos2){
			pos2 = getRandomNum() % sceneryNum;
		}
		
		//ȷ��pos1С��pos2
		if (pos1 > pos2) {
			int tmpPos = pos1;
			pos1 = pos2;
			pos2 = tmpPos;
		}
		
		//��������Ⱦɫ���м䲿��
		for (int i = pos1; i < pos2; i++) {
			int t = newPopulation[k1][i];
			newPopulation[k1][i] = newPopulation[k2][i];
			newPopulation[k2][i] = t;
		}
	}
	
	/**
	 * ��ζԻ���������
	 * �磺123456���153426������2��5�Ի���
	 * @param k Ⱦɫ����
	 */
	private void onVariation(int k){
		//�Ի��������
		int index;
		index = getRandomNum() % sceneryNum;
		newPopulation[k][index] = getRandomNum() % 2;
	}
	
	/**
	 * �������
	 */
	public ArrayList<Route> solve(){
		//��ʼ����Ⱥ
		initGroup();
		//�����ʼ�ʶ�
		for (int i = 0; i < scale; i++) {
			fitness[i] = this.evaluate(i, oldPopulation[i]);
		}
		// �����ʼ����Ⱥ�и���������ۻ����ʣ�pi[max]
		countRate();
		
		System.out.println("��ʼ��Ⱥ...");
		
		//��ʼ����
		for (curGen = 0; curGen < maxGen; curGen++) {
			evolution();
			// ������ȺnewGroup���Ƶ�����ȺoldGroup�У�׼����һ������
			for (int i = 0; i < scale; i++) {
				for (int j = 0; j < sceneryNum; j++) {
					oldPopulation[i][j] = newPopulation[i][j];
				}
			}
			
			//���㵱ǰ�����ʶ�
			for (int i = 0; i < scale; i++) {
				fitness[i] = this.evaluate(i, oldPopulation[i]);
			}
			
			// ���㵱ǰ��Ⱥ�и���������ۻ����ʣ�pi[max]
			countRate();
		}
		
		selectBestGh();
		
		System.out.println("�����Ⱥ");
		HashMap<String, Route> routeMap = new HashMap<String, Route>();
		//��ó��ж���
		Scenery city = SceneryUtil.getCity(cityId);
		for (int i = 0; i < scale; i++) {
			double sceneTicket = 0.0;
			double hotelPrice = 0.0;
			double hotness = fitness[i];
			double days = 0.0;
			int viewCount = 0;
			String tmpR = "";
			Route route = new Route();
			//��þ����б�
			ArrayList<Scenery> sList = new ArrayList<Scenery>();
			for (int j = 0; j < sceneryNum; j++) {
				if (oldPopulation[i][j] == 1) {
					Scenery scene = sceneryList.get(j);
					sceneTicket += scene.getPrice();
					viewCount += scene.getViewCount();
					days += scene.getVisitDay();
					tmpR += scene.getSid();
					sList.add(scene);
					System.out.print(scene.getSname() + ",");
				}
			}
			//����Ƽ��ľƵ��б�
			ArrayList<Hotel> hotelList = new ArrayList<Hotel>();
			String hotelStr = recommendHotel[i];
			if (hotelStr != null && !hotelStr.equals("")) {
				String[] arr = hotelStr.split(",");
				for (String hSid : arr) {
					Hotel hotel = hotelMap.get(hSid);
					hotelPrice += hotel.getPrice();
					hotelList.add(hotel);
				}
			}
			
			String uid = AppUtil.md5(tmpR);
			String sid = city.getSid();
			String ambiguitySname = city.getAmbiguitySname();
			String sname = city.getSname();
			String surl = city.getSurl();
			double sumPrice = hotelPrice + sceneTicket;
			
			route.setUid(uid);
			route.setSid(sid);
			route.setAmbiguitySname(ambiguitySname);
			route.setSname(sname);
			route.setSurl(surl);
			route.setUpDay(upDay);
			route.setDownDay(downDay);
			route.setVisitDay(days);
			route.setHotness(hotness);
			route.setViewCount(viewCount);
			route.setHotelPrice(hotelPrice);
			route.setSceneTicket(sceneTicket);
			route.setSumPrice(sumPrice);
			route.setSceneryList(sList);
			route.setHotelList(hotelList);
			if (!routeMap.containsKey(uid)) {
				routeMap.put(uid, route);
			}
			System.out.print("  ������" + days + " --�۸�" + sceneTicket + " --�ȶ�:" + hotness);
			System.out.print(" �ʶȣ�" + fitness[i] + " �Ƶ꣺" + recommendHotel[i]);
			System.out.println();
		}
		
		ArrayList<Route> routeList = new ArrayList<Route>();
		Iterator<String> iter = routeMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Route route = routeMap.get(key);
			routeList.add(route);
		}
		
		Collections.sort(routeList);
		
		
		//------------------------------------
		System.out.println("��ѳ��ȳ��ִ�����");
		System.out.println(bestGen);
		System.out.println("��ѳ���");
		System.out.println(bestLen);
		System.out.println("��ѾƵ꣺");
		System.out.println(bestHotelIds);
		System.out.println("���·����");
		for (int i = 0; i < sceneryNum; i++) {
			System.out.print(bestRoute[i] + ",");
		}
		System.out.println();
		double price = 0.0;
		double hotness = 0.0;
		double days = 0.0;
		for (int i = 0; i < sceneryNum; i++) {
			if (bestRoute[i] == 1) {
				Scenery scene = sceneryList.get(i);
				price += scene.getPrice();
				hotness += scene.getViewCount();
				days += scene.getVisitDay();
				System.out.print(scene.getSname() + ",");
			}
		}
		if (!bestHotelIds.equals("")) {
			System.out.println();
			System.out.println("���㻨�ѣ�" + price +" Ԫ");
			String[] hotelArr = bestHotelIds.split(",");
			for (String sid : hotelArr) {
				Hotel hotel = hotelMap.get(sid);
				price += hotel.getPrice();
				System.out.println("�Ƶ꣺" + sid + "-" + hotel.getPrice() + "Ԫ");
			}
		}
		System.out.print("  ������" + days + " --�۸�" + price + " --�ȶ�:" + hotness);
		System.out.print(" �Ƶ�:" + bestHotelIds);
		System.out.println();
		
		return routeList;
	}
	
	
	public static void main(String[] args) throws IOException{
		long begin = System.currentTimeMillis();
		HashMap<String, Hotel> hotelMap = HotelUtil.getAllHotel();
		
		GaScenery ga = new GaScenery(300, 1000, 0.8, 0.9);
//		ga.init("da666bc57594baeb76b3bcf0",2.0, 3.0, hotelMap);
		ga.init("622bc401f1153f0fd41f74dd",2.0, 3.0, hotelMap);
		
		ArrayList<Route> routeList = ga.solve();
		
		for (Route route : routeList) {
			ArrayList<Scenery> sceneList = GaSort.doRun(route.getSceneryList());
			for (Scenery scenery : sceneList) {
				System.out.print(scenery.getSname() + ",");
			}
			System.out.println("--�ȶȣ�" + route.getHotness() + "--�۸�"+route.getSumPrice());
		}
		
		System.out.println("�ܹ���" + routeList.size() +"��·��");
		long end = System.currentTimeMillis();
		long time = (end - begin);
		System.out.println();
		System.out.println("��ʱ��"+ time +" ms");
	}
	
	
	
	
	
	
	
	
	

}
