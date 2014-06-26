package Ui;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;

import Util.FileUtil;
import Util.AppUtil;

/**
 * 索引建立
 * @author 
 *
 */
public class TestIndex {

	public static void main(String[] args){
		long begin = System.currentTimeMillis();
//		write();
		index();
		long end = System.currentTimeMillis();
		System.out.println("" + (end - begin) + "ms");
	}
	
	/* *
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
				String content = AppUtil.readFile(file);
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
	/* *
	 * 解析数据库，返回需要的字段
	 * @param content
	 * @return
	 */
	public static ResultSet getDbData()
	{
		ResultSet rSet=executeQuery("select sid,ambiguity_sname,surl from t_scenery", null);
		return rSet;
	}
	/* *
	 * 建立数据库索引
	 */
	@SuppressWarnings("deprecation")
	public static void index()
	{
		ResultSet rSet=getDbData();
		IndexWriter writer = null;
		Directory directory = null;
		try {
			Analyzer analyzer = new IKAnalyzer(false);
			directory = new SimpleFSDirectory(new File("./index/"));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(directory, config);
			try {
				while (rSet.next()) {
					Field field1=new Field("sid",rSet.getString("sid"), Field.Store.YES, Field.Index.NO);
					Field field2=new Field("ambiguity_sname",rSet.getString("ambiguity_sname"), Field.Store.YES, Field.Index.ANALYZED);
					Field field3=new Field("surl",rSet.getString("surl"), Field.Store.YES, Field.Index.NO);
					Document doc=new Document();
					doc.add(field1);
					doc.add(field2);
					doc.add(field3);
					writer.addDocument(doc);
				}
		        writer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/* *
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
			String sname = dataObj.getString("ambiguity_sname");
			
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
	/* **
	 * 连接数据库
	 */
	public  static Connection GetConnection()
	{
		Connection conn=null;
		try {
			Driver jdbcDriver=new Driver();
			DriverManager.registerDriver(jdbcDriver);
//			String dbUrl="jdbc:mysql://127.0.0.1:3306/travel";
			String dbUrl= "jdbc:mysql://127.0.0.1:3306/travel?characterEncoding=gbk";
			String dbUser="root";
			String dbPwd="";
			conn=(Connection) DriverManager.getConnection(dbUrl, dbUser, dbPwd);
		} catch (SQLException e) {
			System.out.println("连接服务器失败");
			e.printStackTrace();
		}
		return conn;
	}
	/*
	 *数据库 查询操作
	 *@params sql
	 */
	public static ResultSet executeQuery(String sql,String[] params)
	{
		Connection conn=GetConnection();
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try {
			preparedStatement=conn.prepareStatement(sql);
			if(params!=null)
			{
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setString(i+1, params[i]);
				}
			}
			resultSet=preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			
		}
		return resultSet;
	}
	
	
}
