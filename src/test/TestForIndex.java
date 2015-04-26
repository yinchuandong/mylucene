package test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestForIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CreateIndex();
	}

	public static void CreateIndex() {
		File dir = new File("F:\\content");
		File indexDir = new File("F:\\index");
		IndexWriter indexWriter = null;
		long startTime = 0l;
		try {
			Analyzer luceneAnalyzer = new IKAnalyzer();
			Directory directory = new SimpleFSDirectory(indexDir);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
					luceneAnalyzer);
			indexWriter = new IndexWriter(directory, config);
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.exists() && file.getName().endsWith(".txt")) {
					System.out.println("文件 " + file.getCanonicalPath()
							+ " 开始索引");
					FileReader reader = new FileReader(file);
					Document document = new Document();
					document.add(new Field("name", file.getName(),
							Field.Store.YES, Field.Index.ANALYZED));
					document.add(new Field("content", reader));
					document.add(new Field("path", file.getPath(),
							Field.Store.YES, Field.Index.ANALYZED));
					indexWriter.addDocument(document);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		long endTime = new Date().getTime();
		System.out.println("用时：" + (endTime - startTime) + " 毫秒");
		System.out.println("索引路径：" + indexDir.getPath());
	}
}
