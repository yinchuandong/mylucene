package Ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.crypto.Data;

import net.sf.json.JSONObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import Util.FileUtil;

/**
 * 索引建立
 * @author yinchuandong
 *
 */
public class TestIndex {

	public static void main(String[] args){
		long begin = System.currentTimeMillis();
		write();
		long end = System.currentTimeMillis();
		System.out.println("" + (end - begin) + "ms");
	}
	
	/**
	 * 将文件夹中的文件全部读取，按字段建立索引
	 */
	public static void write(){
		IndexWriter writer = null;
		Directory directory = null;
		try {
			Analyzer analyzer = new IKAnalyzer(false);
			directory = new SimpleFSDirectory(new File("./index/"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			writer = new IndexWriter(directory, config);
			
			File dir = new File("E:\\web");
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String content = FileUtil.readFile(file);
				ArrayList<Field> fieldList = parseJson(content);
				Document doc = new Document();
				for (Field field : fieldList) {
					doc.add(field);
				}
				writer.addDocument(doc);
				System.out.print("第" + i + "个");
//				System.out.println(content);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (writer != null) {
					System.out.println("close writer");
					writer.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			try {
				if (directory != null) {
					System.out.println("close directory");
					directory.clearLock("write");
					directory.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析json的文件，返回需要的字段
	 * @param content
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<Field> parseJson(String content){
		ArrayList<Field> fieldList = new ArrayList<Field>();
		try {
			JSONObject jsonObj = JSONObject.fromObject(content);
			JSONObject dataObj = jsonObj.getJSONObject("data");
			String sid = dataObj.getString("sid");
			String surl = dataObj.getString("surl");
			String sname = dataObj.getString("sname");
			
			Field fSid = new Field("sid", sid, Field.Store.YES, Field.Index.NO);
			Field fSurl = new Field("surl", surl, Field.Store.YES, Field.Index.NO);
			Field fSname = new Field("sname", sname, Field.Store.YES, Field.Index.ANALYZED);
			fieldList.add(fSid);
			fieldList.add(fSurl);
			fieldList.add(fSname);
			
			System.out.println(sname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldList;
	}
	
	
	
}
