package Ui;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Hit;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 测试索引分析器
 * @author yinchuandong
 *
 */
public class TestIkAnalyzer {

	//Lucene Document的域名
	String fieldName = "text";
	//检索内容
	String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";
	Analyzer analyzer = null;
	Directory directory = null;
	IndexWriter iWriter = null;
	IndexReader iReader = null;
	IndexSearcher iSeacher = null;
	
	public TestIkAnalyzer(){
		try {
			analyzer = new IKAnalyzer(false);
			directory = new SimpleFSDirectory(new File("./index/"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 写入索引
	 */
	@SuppressWarnings("deprecation")
	public void writeIndex(){
		try {
//			IndexWriterConfig iWriterConfig = new IndexWriterConfig(Version.LUCENE_48, analyzer);
//			//每次都重新创建索引，否则可选create_or_append
//			iWriterConfig.setOpenMode(OpenMode.CREATE);
//			
//			iWriter = new IndexWriter(directory, iWriterConfig);
//			Document doc = new Document();
//			doc.add(new Field("ID", "10000", Field.Store.YES, Field.Index.NOT_ANALYZED));
//			doc.add(new Field(fieldName, text, Field.Store.YES,	Field.Index.ANALYZED));
//			iWriter.addDocument(doc);
			IndexWriterConfig iWriterConfig = new IndexWriterConfig(Version.LUCENE_48, analyzer);
			//每次都重新创建索引，否则可选create_or_append
			iWriterConfig.setOpenMode(OpenMode.CREATE);
			
			iWriter = new IndexWriter(directory, iWriterConfig);
			Document doc1 = new Document();
			Document doc2 = new Document();
			Document doc3 = new Document();
			Document doc4 = new Document();
			Document doc5 = new Document();
			Document doc6 = new Document();
			Document doc7 = new Document();
			Document doc8 = new Document();
			
			Field field1 = new Field(fieldName, "钢铁是怎样炼成的", Field.Store.YES, Field.Index.ANALYZED);
			Field field2 = new Field(fieldName, "英雄儿女", Field.Store.YES, Field.Index.ANALYZED);
			Field field3 = new Field(fieldName, "篱笆女儿和狗", Field.Store.YES, Field.Index.ANALYZED);
			Field field4 = new Field(fieldName, "女人是水做的", Field.Store.YES, Field.Index.ANALYZED);
			Field field5 = new Field(fieldName, "我的兄弟和女儿", Field.Store.YES, Field.Index.ANALYZED);
			Field field6 = new Field(fieldName, "白毛女", Field.Store.YES, Field.Index.ANALYZED);
			Field field7 = new Field(fieldName, "钢铁的世界", Field.Store.YES, Field.Index.ANALYZED);
			Field field8 = new Field(fieldName, "钢铁战士，从那时起，by a time 我就不是坏人了", Field.Store.YES, Field.Index.ANALYZED);
			
			doc1.add(field1);
			doc2.add(field2);
			doc3.add(field3);
			doc4.add(field4);
			doc5.add(field5);
			doc6.add(field6);
			doc7.add(field7);
			doc8.add(field8);
			
			iWriter.addDocument(doc1);
			iWriter.addDocument(doc2);
			iWriter.addDocument(doc3);
			iWriter.addDocument(doc4);
			iWriter.addDocument(doc5);
			iWriter.addDocument(doc6);
			iWriter.addDocument(doc7);
			iWriter.addDocument(doc8);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				directory.close();
				iWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 搜索
	 */
	@SuppressWarnings("deprecation")
	public void search(){
		try{
			iReader = IndexReader.open(directory);
			iSeacher = new IndexSearcher(iReader);
			
			Term t1 = new Term("sname","白云");
			Term t2 = new Term("sname", "云山");
			Query q1 = new TermQuery(t1);
			Query q2 = new TermQuery(t2);
			BooleanQuery booleanQuery = new BooleanQuery();
			booleanQuery.add(q1, BooleanClause.Occur.MUST);
			booleanQuery.add(q2, BooleanClause.Occur.MUST);
			
//			Term t3 = new Term("sname","长隆");
//			Query q3 = new TermQuery(t3);
//			TopDocs topDocs = iSeacher.search(q3, 10);
			
			TopDocs topDocs = iSeacher.search(booleanQuery, 10);
			
			System.out.println("hit: " + topDocs.totalHits);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document targetDoc = iSeacher.doc(scoreDoc.doc);
				System.out.println(targetDoc.toString());
				System.out.println(scoreDoc.score);
				System.out.println(scoreDoc.doc);
//				System.out.println("content: " + targetDoc.get(fieldName));
//				System.out.println(iSeacher.explain(booleanQuery, scoreDoc.doc));
				System.out.println("----------------------------------");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				iReader.close();
				directory.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
		
	public static void main(String[] args) throws IOException{
		long begin = System.currentTimeMillis();
		TestIkAnalyzer analyzer = new TestIkAnalyzer();
//		analyzer.writeIndex();
		analyzer.search();
//		analyzer.testSegment();
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - begin) + "ms");
	}
	
	/**
	 * 测试分词
	 * @throws IOException
	 */
	public void testSegment() throws IOException{
		String str = "广州长隆欢乐世界白毛女，从那时起，by a time 我就不是坏人了" ; 
	    StringReader reader = new StringReader(str); 
	    IKSegmenter ik = new IKSegmenter(reader,false);//当为true时，分词器进行最大词长切分 
	    Lexeme lexeme = null; 
	    while((lexeme = ik.next())!=null) 
	    System.out.println(lexeme.getLexemeText());
	}
	
	
}
