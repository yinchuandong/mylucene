package Ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestJson {


	public void testObj(){
		ArrayList<User> list = new ArrayList<User>();
		for(int i=0; i<10; i++){
			User user = new TestJson.User("username" + i, "password" + i);
			list.add(user);
		}
		
		JSONArray jsonArray = JSONArray.fromObject(list);
		
		System.out.println(jsonArray.toString());
	}
	
	public static void main(String[] args){
//		TestJson model = new TestJson();
//		model.testObj();
		HashMap<String, String> map = new HashMap<>();
		map.put("username", "Òü´¨¶«");
		JSONObject object = JSONObject.fromObject(map);
		System.out.println();
	}
	
	public class User{
		public String username = "";
		public String password = "";
		
		public User(String username, String password){
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		
	}
}
