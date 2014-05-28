package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class AppUtil {

	
	/**
	 * ��ȡ�ļ�
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
	 * ������תΪunicode����ֹ��������
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
		
		System.out.println(toUnicode("����ɽ"));
		
	}
}
