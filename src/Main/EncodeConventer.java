package Main;

import java.io.File;

import Util.AppUtil;

public class EncodeConventer {

	public static void run(){
		File dir = new File("E:\\traveldata\\routes3");
		File expDir = new File("E:\\traveldata\\routes3-unicode");
		
		File[] supFiles = dir.listFiles();
		for (int i = 0; i < supFiles.length; i++) {
			File subDir = supFiles[i];
			File expSubDir = new File(expDir.getPath() + "\\" + subDir.getName());
			if (!expSubDir.exists()) {
				expSubDir.mkdirs();
			}
			File[] subFiles = subDir.listFiles();
			for (int j = 0; j < subFiles.length; j++) {
				File sfile = subFiles[j];
				String content = AppUtil.readFile(sfile);
				content = AppUtil.toUnicode(content);
				String fileName = expSubDir + "\\" + sfile.getName();
				AppUtil.exportFile(fileName, content);
				System.out.println(fileName);
				System.out.println("---------------------处理完第" + j +"个景点-------------");
			}
			System.out.println("处理完第" + i +"个城市-------------");
		}
		
	}
	
	/**
	 * 对scenery文件进行编码
	 */
	public static void encodeScenery(){
		File dir = new File("E:\\traveldata\\webAll");
		File expDir = new File("E:\\traveldata\\webAll-unicode");
		if (!expDir.exists()) {
			expDir.mkdirs();
		}
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().indexOf("-1.json") == -1) {
				continue;
			}
			String content = AppUtil.readFile(file);
			content = AppUtil.toUnicode(content);
			String fileName = expDir.getPath() + "\\" + file.getName();
			AppUtil.exportFile(fileName, content);
			System.out.println("处理完第" + i +"个城市-------------" + file.getName());
		}
		
	}
	
	public static void main(String[] args){
		long begin = System.currentTimeMillis();
		run();
//		encodeScenery();
		
		long end = System.currentTimeMillis();
		long time = (end - begin);
		System.out.println();
		System.out.println("耗时："+ time +" ms");
	}
}
