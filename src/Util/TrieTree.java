package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



import Util.AppUtil;

/**
 * ����trie����������������ʾģ��
 * @author yinchuandong
 *
					   _ooOoo_
					  o8888888o
					  88" . "88
					  (| -_- |)
					  O\  =  /O
				   ____/`---'\____
				 .'  \\|     |//  `.
				/  \\|||  :  |||//  \
			   /  _||||| -:- |||||-  \
			   |   | \\\  -  /// |   |
			   | \_|  ''\---/''  |   |
			   \  .-\__  `-`  ___/-. /
			  ___`. .'  /--.--\  `. . __
		   ."" '<  `.___\_<|>_/___.'  >'"".
		  | | :  `- \`.;`\ _ /`;.`/ - ` : | |
		  \  \ `-.   \_ __\ /__ _/   .-` /  /
	 ======`-.____`-.___\_____/___.-`____.-'======
					    `=---='
	 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
				  ���汣��       ����BUG
*/
public class TrieTree {

	private TrieNode root = null;
	private HashMap<String, Integer> sentenceMap = null;
	
	public TrieTree(){
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
		
	}
	
	/**
	 * ����ָ����ǰ׺
	 * @param word
	 * @return
	 */
	public ArrayList<Sentence> find(String word){
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
				return result;
			}
		}
		
		//���keyword�Ѿ�����һ����,��:baiyunshan
		if(node.isSentence){
			result.add(new Sentence(prefix, node.getCount()));
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
				//������ַ��Ѿ�����һ��keyword�ʣ�����뵽result��
				if(tmpNode.isSentence){
					Sentence sentence = new Sentence(tmpStr, tmpNode.getCount());
					result.add(sentence);
				}
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
		System.out.println("-----------------");
		long begin = System.currentTimeMillis();
		
		
		long end = System.currentTimeMillis();
		System.out.println("��ʱ��" + (end - begin));
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
