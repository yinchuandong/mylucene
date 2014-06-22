package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class AppUtil {

	/**
	 * md5加密算法
	 * @param str
	 * @return
	 */
	static public String md5(String str) {
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (algorithm != null) {
			algorithm.reset();
			algorithm.update(str.getBytes());
			byte[] bytes = algorithm.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				hexString.append(Integer.toHexString(0xFF & b));
			}
			return hexString.toString();
		}
		return "";

	}
	
	/**
	 * 读取文件
	 * @param file
	 * @return
	 */
	public static String readFile(File file){
		String result = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while((tmp = reader.readLine()) != null){
				result += tmp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 将中文转为unicode，防止传输乱码
	 * @param strText
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toUnicode(String strText) { 
		char c; 
		String strRet = ""; 
		int intAsc; 
		String strHex; 
		for (int i = 0; i < strText.length(); i++) { 
			c = strText.charAt(i); 
			intAsc = (int) c; 
			if (intAsc > 128) { 
				strHex = Integer.toHexString(intAsc); 
				strRet += "\\u" + strHex; 
			} else { 
				strRet = strRet + c; 
			} 
		} 
		return strRet; 
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
//		File dir = new File("E:\\web");
//		File[] files = dir.listFiles();
//		for (int i = 0; i < 1; i++) {
//			File file = files[i];
//			String content = FileUtil.readFile(file);
//			System.out.println(content);
//		}
		
		System.out.println(toUnicode("白云山"));
		
	}
}
