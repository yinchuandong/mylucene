package Util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.lang.model.element.VariableElement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchUtil {
	
	private Directory directory = null;
	private IndexReader reader = null;
	private IndexSearcher searcher = null;
	
	public SearchUtil(){
		
	}

	/**
	 * �зִ��������ص�arraylist��
	 * @param word
	 * @return
	 */
	private ArrayList<String> cut(String word){
		ArrayList<String> list = new ArrayList<String>();
		if (word == null || word.equals("")) {
			return list;
		}
	    StringReader reader = new StringReader(word); 
	    IKSegmenter ik = new IKSegmenter(reader,false);//��Ϊtrueʱ���ִ����������ʳ��з� 
	    Lexeme lexeme = null; 
	    
	    try {
			while((lexeme = ik.next())!=null) {
				list.add(lexeme.getLexemeText());
				System.out.println(lexeme.getLexemeText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return list;
	}
	
	/**
	 * �������н��м���
	 * @param word
	 * @return
	 */
	public String retrieve(String word){
		JSONObject resultJson = JSONObject.fromObject("{}");
		if (word == null || word.equals("")) {
			resultJson.put("data", JSONArray.fromObject("[]"));
			resultJson.put("info", AppUtil.toUnicode("�ؼ��ʲ���Ϊ��"));
			resultJson.put("status", "0");
			return resultJson.toString().replaceAll("\\\\u", "\\u");
		}
		
		//-------������--------
		ArrayList<String> list = this.cut(word);
		try {
			directory = new SimpleFSDirectory(new File("E:\\traveldata\\index"));
			reader = IndexReader.open(directory);
			searcher = new IndexSearcher(reader);
		} catch (IOException e) {
			e.printStackTrace();
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			resultJson.put("data", JSONArray.fromObject("[]"));
			resultJson.put("info", AppUtil.toUnicode("������ʧ��"));
			resultJson.put("status", "0");
			return resultJson.toString().replaceAll("\\\\u", "\\u");
		}
		
		BooleanQuery booleanQuery = new BooleanQuery();
		
		for (String key : list) {
			Query query = new TermQuery(new Term("ambiguity_sname", key));
			booleanQuery.add(query, BooleanClause.Occur.MUST);
		}
		ArrayList<Scenery> dataList = new ArrayList<Scenery>();
		try {
			TopDocs topDocs = searcher.search(booleanQuery, 10);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document targetDoc = searcher.doc(scoreDoc.doc);
				String sid = targetDoc.get("sid");
				String ambiguitySname = AppUtil.toUnicode(targetDoc.get("ambiguity_sname"));
				String surl = targetDoc.get("surl");
				dataList.add(new Scenery(sid, ambiguitySname, surl));
			}
		} catch (IOException e) {
			e.printStackTrace();
			resultJson.put("data", JSONArray.fromObject("[]"));
			resultJson.put("info", AppUtil.toUnicode("����ʧ��"));
			resultJson.put("status", "0");
			return resultJson.toString().replaceAll("\\\\u", "\\u");
		} finally{
			try {
				reader.close();
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		resultJson.put("data", JSONArray.fromObject(dataList));
		resultJson.put("info", AppUtil.toUnicode("���سɹ�"));
		resultJson.put("status", "1");
		return resultJson.toString().replaceAll("\\\\u", "\\u");
	}
	
	/**
	 * �ⲿ���õľ�̬����
	 * @param word
	 * @return
	 */
	public static String search(String word){
		SearchUtil searchUtil = new SearchUtil();
		String result = searchUtil.retrieve(word);
		return result;
	}
	
	public class Scenery{
		public String sid = "";
		public String ambiguitySname = "";
		public String surl = "";
		
		public Scenery(String sid, String ambiguitySname, String surl){
			this.sid = sid;
			this.ambiguitySname = ambiguitySname;
			this.surl = surl;
		}

		public String getSid() {
			return sid;
		}

		public void setSid(String sid) {
			this.sid = sid;
		}

		public String getAmbiguitySname() {
			return ambiguitySname;
		}

		public void setAmbiguitySname(String ambiguitySname) {
			this.ambiguitySname = ambiguitySname;
		}

		public String getSurl() {
			return surl;
		}

		public void setSurl(String surl) {
			this.surl = surl;
		}
		
		
	}
	
	
	
	public static void main(String[] args){
		System.out.println("====================");
		SearchUtil searchUtil = new SearchUtil();
		String result = searchUtil.retrieve("��");
		System.out.println(result);
	}
	
}
