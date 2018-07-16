package com.mochat.redis;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.mochat.listener.MyApplicationContextListener;
import com.mochat.threadPool.RandomThreadPool;
import com.mochat.websocket.MoChatSessionManager;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

public class RedisMsgListener extends JedisPubSub{
	
	
	    @Override  
	    public void unsubscribe() {  
	        super.unsubscribe();  
	    }  
	  
	    @Override  
	    public void unsubscribe(String... channels) {  
	        super.unsubscribe(channels);  
	    }  
	  
	    @Override  
	    public void subscribe(String... channels) {  
	        super.subscribe(channels);  
	    }  
	  
	    @Override  
	    public void psubscribe(String... patterns) {  
	        super.psubscribe(patterns);  
	    }  
	  
	    @Override  
	    public void punsubscribe() {  
	        super.punsubscribe();  
	    }  
	  
	    @Override  
	    public void punsubscribe(String... patterns) {  
	        super.punsubscribe(patterns);  
	    }  
	  
	    @Override  
	    public void onMessage(String channel, String message) {
	        RandomThreadPool.send(message);
	       
	    }  
	  
	    @Override  
	    public void onPMessage(String pattern, String channel, String message) {  
	  
	    }  
	  
	    @Override  
	    public void onSubscribe(String channel, int subscribedChannels) {  
	        System.out.println("channel:" + channel + "is been subscribed:" + subscribedChannels);  
	    }  
	  
	    @Override  
	    public void onPUnsubscribe(String pattern, int subscribedChannels) {  
	  
	    }  
	  
	    @Override  
	    public void onPSubscribe(String pattern, int subscribedChannels) {  
	  
	    }  
	  
	    @Override  
	    public void onUnsubscribe(String channel, int subscribedChannels) {  
	        System.out.println("channel:" + channel + "is been unsubscribed:" + subscribedChannels);  
	    }  
}
