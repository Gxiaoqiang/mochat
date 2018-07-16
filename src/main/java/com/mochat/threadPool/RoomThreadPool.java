package com.mochat.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mochat.netty.constant.NettyContextManage;
import com.util.MoThreadFactory;
import com.util.MoThreadPoolExecutor;

import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class RoomThreadPool {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomThreadPool.class);//Logger.getLogger(RandomThreadPool.class);

	//private static final Logger LOGGER = Logger.getLogger(RoomThreadPool.class);
	private static final LinkedBlockingQueue<Runnable> LINKED_BLOCKING_QUEUE = new LinkedBlockingQueue<Runnable>();
	
	private static final MoThreadPoolExecutor MO_THREAD_POOL_EXECUTOR = new MoThreadPoolExecutor(6, 10, 600, TimeUnit.SECONDS, LINKED_BLOCKING_QUEUE, new MoThreadFactory("room"));
	private RoomThreadPool() {}
	
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
			if(!jsonObject.containsKey("userId")||!jsonObject.containsKey("roomId")) {
				return;
			}
	        try {
	        
	        	String roomId = jsonObject.getString("roomId");
	        	if(roomId == null) {
	        		return;
	        	}
	        	
	        	ChannelGroup channelGroup = NettyContextManage.CHANNELGROUP_MAP.get(roomId);
	        	if(channelGroup == null) {
	        		return ;
	        	}
	        	TextWebSocketFrame tws = new TextWebSocketFrame(jsonObject.toJSONString());
	        	channelGroup.writeAndFlush(tws);
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
