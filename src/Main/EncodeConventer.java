package Main;

import java.io.File;

import Util.AppUtil;

public class EncodeConventer {

	public static void run(){
		File dir = new File("E:\\traveldata\\routes3\\shenzhen");
		File expDir = new File("E:\\traveldata\\route-unicode\\shenzhen");
		if (!expDir.exists()) {
			expDir.mkdirs();
		}
		
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String content = AppUtil.readFile(file);
			content = AppUtil.toUnicode(content);
			AppUtil.exportFile(expDir + "\\" + file.getName(), content);
			System.out.println(content);
			System.out.println("处理完第" + i +"个");
		}
		
	}
	
	public static void main(String[] args){
		run();
	}
}
