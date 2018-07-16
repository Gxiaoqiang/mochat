package com.mochat.websocket;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.mochat.cache.ThreadLocalCache;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.listener.MyApplicationContextListener;
import com.mochat.model.RandomMessageBody;
import com.mochat.model.UserInfo;
import com.mochat.rabbitmq.RabbitRandomPublish;
import com.mochat.redis.CustomRedisClusters;

import redis.clients.jedis.JedisCluster;

public class MoChatWebSocketHandler implements WebSocketHandler  {

	private static final Logger logger = Logger.getLogger(MoChatWebSocketHandler.class);
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		try {
			CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
			UserInfo currentUserInfo = ThreadLocalCache.get();
			String userId = currentUserInfo.getUserId();
			WebSocketSession session2 = null;
			String toUserId = null;
			if(userId != null) {
				session2 = MoChatSessionManager.SESSION_MAP.get(userId);
				toUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
			}
			 if(toUserId != null) {
		 	    	currentUserInfo.setToUserId(toUserId);
		 	    	ThreadLocalCache.set(currentUserInfo);
		 	}
			if(session2 != null&&session2.isOpen()) {
				//MoChatSessionManager.SESSION_ID_MAP.remove(session2);
				session = session2;
				return;
			}
			if(userId != null) {
				MoChatSessionManager.SESSION_MAP.remove(userId);
				MoChatSessionManager.SESSION_MAP.put(userId, session);
				MoChatSessionManager.SESSION_ID_MAP.put(session, userId);
			}
	 	   
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}


	public void handleTransportError(WebSocketSession webSocketSession, Throwable exception) throws Exception {
		closeSession(webSocketSession);
	}
	
	public void closeSession(WebSocketSession webSocketSession)throws Exception{
        UserInfo userInfo = ThreadLocalCache.get();
		String chatType = userInfo.getChatType();
		if(StringUtils.equals(Constants.RANDOM_CHAT, chatType)) {
			closeRandomSession(webSocketSession);
		}
		if(StringUtils.equals(Constants.ROOM_CHAT, chatType)) {
			closeRoomSession(webSocketSession);
		}
	}
	
	
	private final void closeRoomSession(WebSocketSession webSocketSession)throws Exception{
		
	}
	private final void closeRandomSession(WebSocketSession session)throws Exception{
		try {
			RabbitRandomPublish rabbitRandomPublish = (RabbitRandomPublish) MyApplicationContextListener.getBean("rabbitRandomPublish");
			CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
			String userId = null;
			String toUserId = null;
			userId = MoChatSessionManager.SESSION_ID_MAP.get(session);
			 if(userId != null) {
				 toUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
			 }
			if(toUserId != null) {
				 jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, toUserId);
			}
	 	    if(userId != null) {
	 	    	jedisCluster.srem(RedisConstants.WAIT_SET, userId);
	 	    	jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, userId);
	 	    	jedisCluster.srem(RedisConstants.ON_LINE_SET, userId);
	 	    	MoChatSessionManager.SESSION_MAP.remove(userId);
	 	    }
	 	    if(userId!=null&&toUserId!=null) {
	 	    	RandomMessageBody msBody = new RandomMessageBody();
				msBody.setUserId(userId);
				msBody.setToUserId(toUserId);
				msBody.setDisConnectFlag(true);
				rabbitRandomPublish.send("R",JSONObject.toJSON(msBody).toString());
				//jedisCluster.publish(RedisConstants.SUB_PUB_CHANNEL, JSONObject.toJSON(msBody).toString());
	 	    }
	 	    MoChatSessionManager.SESSION_ID_MAP.remove(session);
	 	    ThreadLocalCache.remove();//删除cache
	 	    if(session.isOpen()) {
	 	    	 session.close();
	 	    }
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		// TODO Auto-generated method stub
		closeSession(session);
	}
	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// TODO Auto-generated method stub
		send(session, message);
	}

	/**
	 * 异常不抛出，
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	public void send(WebSocketSession session,WebSocketMessage<?> message) throws Exception{
	
		try {
			if(message instanceof TextMessage) {
				message = (TextMessage)message;
				String messageStr =  (String)message.getPayload();
				if(messageStr.toString().contains("HeartBeat")) {
					return;
				}
				if(messageStr.contains("random")&&messageStr.contains("userId")) {
					randomSend( session, messageStr);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		
		
	}
	/**
	 * 随机聊天发送信息
	 * @param session
	 * @param messageStr
	 * @throws Exception
	 */
	public void randomSend(WebSocketSession session,String messageStr) throws Exception{
		
		JSONObject jsonObject = JSONObject.parseObject(messageStr.toString());
		RabbitRandomPublish rabbitRandomPublish = (RabbitRandomPublish) MyApplicationContextListener.getBean("rabbitRandomPublish");
		CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
		JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
		String userId = jsonObject.getString("userId");
		String redisToUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
		if(redisToUserId == null) {
			jsonObject.put("disConnectFlag", true);
			session.sendMessage(new TextMessage(jsonObject.toString()));
			return;
		}
		jsonObject.put("toUserId", redisToUserId);
		WebSocketSession toUserSession = MoChatSessionManager.SESSION_MAP.get(redisToUserId);
		if(toUserSession != null) {
			toUserSession.sendMessage(new TextMessage(jsonObject.toString()));
			return;
		}
		rabbitRandomPublish.send("R",jsonObject.toString());
	}
}
