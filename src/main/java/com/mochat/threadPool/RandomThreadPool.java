package com.mochat.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.mochat.constant.RedisConstants;
import com.mochat.listener.MyApplicationContextListener;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.websocket.MoChatSessionManager;
import com.util.MoThreadFactory;
import com.util.MoThreadPoolExecutor;

import redis.clients.jedis.JedisCluster;

public class RandomThreadPool {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomThreadPool.class);//Logger.getLogger(RandomThreadPool.class);
	private static final LinkedBlockingQueue<Runnable> LINKED_BLOCKING_QUEUE = new LinkedBlockingQueue<Runnable>();
	
	private static final MoThreadPoolExecutor MO_THREAD_POOL_EXECUTOR = new MoThreadPoolExecutor(6, 10, 600, TimeUnit.SECONDS, LINKED_BLOCKING_QUEUE, new MoThreadFactory("random"));
	private RandomThreadPool() {}
	
	public static void send(String msg) {
		MO_THREAD_POOL_EXECUTOR.execute(new SendRunable(msg));
	}
	
	private static class SendRunable implements Runnable{

		private String message;
		
		public  SendRunable(String msg) {
          this.message = msg;
		}
		
		@Override
		public void run() {
			
			JSONObject jsonObject = JSONObject.parseObject(message);
			if(!jsonObject.containsKey("userId")||!jsonObject.containsKey("toUserId")) {
				return;
			}
	        String toUserId = jsonObject.getString("toUserId");//
	        String userId = jsonObject.getString("userId");
	        try {
	        WebSocketSession webSocketSession = null;
	        if(toUserId != null) {
	        	webSocketSession =	MoChatSessionManager.SESSION_MAP.get(toUserId);
	        }
	        LOGGER.info(message+"linstener message +||"+webSocketSession);
	        if(webSocketSession == null) {
	        	return ;
	        }
	        JedisCluster jedisCluster = null;
	        boolean flag = jsonObject.getBoolean("disConnectFlag");
	        if(flag == true) {
	        	//MoChatSessionManager.SESSION_MAP.remove(toUserId);
	        	CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	     	    jedisCluster = customRedisClusters.getJedisCluster();
	     	    jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, userId);
	   		    jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, toUserId);
	     	    /*try {
	     	    	if(webSocketSession != null) {
	     	    		webSocketSession.close();
	     	    	}
	     	    	MoChatSessionManager.SESSION_MAP.remove(toUserId);
	     	    	MoChatSessionManager.SESSION_ID_MAP.remove(webSocketSession);
					//WebSocketSession toUserSession = MoChatSessionManager.SESSION_MAP.get(toUserId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	     	    
	        }
	        	//if(webSocketSession.isOpen()) {
		        	webSocketSession.sendMessage(new TextMessage(jsonObject.toJSONString()));
	        	//}
	        }catch(Exception e) {
	        	e.printStackTrace();
	        }finally {
	        	/*try {
					jedisCluster.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	        }
			
		}
		
	}
}
