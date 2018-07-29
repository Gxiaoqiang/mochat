package com.mochat.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.mochat.constant.RedisConstants;
import com.mochat.listener.MyApplicationContextListener;
import com.mochat.netty.constant.NettyContextManage;
import com.mochat.netty.server.BaseWebSocketServerHandler;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.websocket.MoChatSessionManager;
import com.util.MoThreadFactory;
import com.util.MoThreadPoolExecutor;

import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.JedisCluster;

public class NettyRandomThreadPool {

	private static final Logger LOGGER = Logger.getLogger(NettyRandomThreadPool.class);
	private static final LinkedBlockingQueue<Runnable> LINKED_BLOCKING_QUEUE = new LinkedBlockingQueue<Runnable>();
	
	private static final MoThreadPoolExecutor MO_THREAD_POOL_EXECUTOR = new MoThreadPoolExecutor(6, 10, 600, TimeUnit.SECONDS, LINKED_BLOCKING_QUEUE, new MoThreadFactory("SEMD_MSG"));
	private NettyRandomThreadPool() {}
	
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
			if(message == null) {
				return;
			}
			JSONObject jsonObject = JSONObject.parseObject(message);
			if(!jsonObject.containsKey("userId")||!jsonObject.containsKey("toUserId")) {
				return;
			}
	        String toUserId = jsonObject.getString("toUserId");//
	        String userId = jsonObject.getString("userId");
	        try {
	        ChannelHandlerContext channelHandlerContext = null;
	        if(toUserId != null) {
	        	channelHandlerContext = NettyContextManage.ID_CTX_MAP.get(toUserId);
	        }
	        
	        LOGGER.info(message+"linstener message +||"+channelHandlerContext);
	        if(channelHandlerContext == null) {
	        	return ;
	        }
	        JedisCluster jedisCluster = null;
	        boolean flag = jsonObject.getBoolean("disConnectFlag");
	        if(flag == true) {
	        	CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	     	    jedisCluster = customRedisClusters.getJedisCluster();
	     	    jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, userId);
	   		    jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, toUserId);
	           }
	        	BaseWebSocketServerHandler.pushRandom(channelHandlerContext, message);
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
