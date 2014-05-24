package Ui;
import redis.clients.jedis.*;

/**
 * ²âÊÔredisÎÄ¼ş»º´æ
 * @author yinchuandong
 *
 */
public class TestRedis {

	
	public static void main(String[] args){
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.set("name2", "yinchuandong");
		jedis.set("name2", "yinchuandong2");
		String value = jedis.get("name2");
		System.out.println(value);
		jedis.disconnect();
	}
	
}
