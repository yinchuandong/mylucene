package Ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import javax.print.DocFlavor.STRING;

import net.sf.json.JSONObject;

import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.lang.WordUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import Util.FileUtil;

/**
 * ����trie����������������ʾģ��
 * @author yinchuandong
 *
 */
public class TestTrieTree {

	private TrieNode root = null;
	private HashMap<String, Integer> sentenceMap = null;
	
	public TestTrieTree(){
		root = new TrieNode();
		sentenceMap = new HashMap<String, Integer>();
	}
	
	/**
	 * ����һ���ʵ�����
	 * @param word �磺��¡��������
	 * @param viewCount �磺36555
	 */
	public void add(String word, int viewCount){
		//���ڵ�Ϊ��
		TrieNode node = root;
		word = word.trim();
		sentenceMap.put(word, viewCount);
		
		for(int i=0; i<word.length(); i++){
			String key = word.substring(i, i+1);
			if (!node.getChildren().containsKey(key)) {
				TrieNode sub = new TrieNode();
				sub.setWord(key);
				sub.setCount(viewCount);
				node.getChildren().put(key, sub);
			}
			node.setTerminal(false);
			node = node.getChildren().get(key);
		}
		node.setTerminal(true);
		node.setSentence(true);
		System.out.println(word);
		
	}
	
	/**
	 * ����ָ����ǰ׺
	 * @param word
	 * @return
	 */
	public ArrayList<Sentence> find(String word){
		System.out.println("-------------");
		ArrayList<Sentence> result = new ArrayList<Sentence>();
		 
		TrieNode node = root;
		word = word.trim();
		String prefix = "";
		for(int i=0; i<word.length(); i++){
			String key = word.substring(i, i+1);
			if (node.getChildren().containsKey(key)) {
				node = node.getChildren().get(key);
				prefix += node.getWord();
			}else{
				return null;
			}
		}
		
		//�ڵ�ջ������������ʹ��Ľڵ�
		Stack<TrieNode> nodeStack = new Stack<TrieNode>();
		//�ַ�ջ������������ʹ���·�����ַ�
		Stack<String> strStack = new Stack<String>();
		
		//��ʼ����ջ
		Iterator<String> iterator = node.getChildren().keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			nodeStack.push(node.getChildren().get(key));
			strStack.push(prefix);
		}
		
		String tmpStr = "";
		while(!nodeStack.empty()){
			TrieNode tmpNode = nodeStack.pop();
			tmpStr = strStack.pop() + tmpNode.word;
			
			if (tmpNode.isTerminal()) {//������ն˴ʣ��򹹳�һ�����ӣ����뵽����б���
				Sentence sentence = new Sentence(tmpStr, tmpNode.getCount());
				result.add(sentence);
				tmpStr = "";
			}else{
				//��������ն˴ʣ��򽫸ôʵ�childrenѹջ���ȴ�����
				Iterator<String> iterChild = tmpNode.getChildren().keySet().iterator();
				while (iterChild.hasNext()) {
					String key = iterChild.next();
					nodeStack.push(tmpNode.getChildren().get(key));
					strStack.push(tmpStr);
				}
			}
		}
		
		Collections.sort(result);
		
		return result;
	}
	
	public static void main(String[] args) throws IOException{
		TestTrieTree tree = new TestTrieTree();
		
		System.out.println("-----------------");
		long begin = System.currentTimeMillis();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File("./words.txt")));
		String line = null;
		while((line = reader.readLine()) != null){
			String[] arr = line.split(" ");
			String word = arr[0];
			int viewCount = Integer.parseInt(arr[1]);
			tree.add(word, viewCount);
		}
		reader.close();
		ArrayList<Sentence> list = tree.find("��");
		for (Sentence sentence : list) {
			System.out.println(sentence.getViewCount() + ":" + sentence.getWord());
		}
		
		long end = System.currentTimeMillis();
		System.out.println("��ʱ��" + (end - begin));
	}
	
	public static void parseFile() throws FileNotFoundException{
		File dir = new File("E:\\web");
		File[] files = dir.listFiles();
		PrintWriter writer = new PrintWriter(new File("./words.txt"));
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String content = FileUtil.readFile(file);
			try {
				JSONObject jsonObject = JSONObject.fromObject(content);
				JSONObject dataObj = jsonObject.getJSONObject("data");
				String sname = dataObj.getString("sname");
				String viewCount = dataObj.getString("view_count");
				writer.write(sname + " " + viewCount +"\r\n");
				writer.flush();
				System.out.println("��" + i + "��: " + sname);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		writer.close();
	}
	
	/**
	 * �����Ľ������
	 * @author yinchuandong
	 *
	 */
	public class Sentence implements Comparable<Sentence>{
		/**
		 * ���ӣ���:���ݰ���ɽ
		 */
		private String word = null;
		/**
		 * ���������磺36555
		 */
		private int viewCount = 0;
		
		public Sentence(String word, int viewCount){
			this.word = word;
			this.viewCount = viewCount;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}

		@Override
		public int compareTo(Sentence o) {
			if (this.getViewCount() > o.getViewCount()) {
				return -1;
			}else{
				return 1;
			}
		}
		
		
	}
	
	/**
	 * �ֵ�������
	 * @author yinchuandong
	 *
	 */
	public class TrieNode{
		
		private String word = null;
		private HashMap<String, TrieNode> children = null;
		private int count = 0;
		private boolean isSentence = false;
		private boolean isTerminal = false;
		
		public TrieNode(){
			children = new HashMap<String, TrieNode>();
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}
		
		public HashMap<String, TrieNode> getChildren() {
			return children;
		}

		public void setChildren(HashMap<String, TrieNode> children) {
			this.children = children;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public boolean isSentence() {
			return isSentence;
		}

		public void setSentence(boolean isSentence) {
			this.isSentence = isSentence;
		}

		public boolean isTerminal() {
			return isTerminal;
		}

		public void setTerminal(boolean isTerminal) {
			this.isTerminal = isTerminal;
		}

		
		
	}
}
