package com.mochat.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.mochat.cache.ThreadLocalCache;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.exception.MoChatException;
import com.mochat.model.RandomMessageBody;
import com.mochat.model.UserInfo;
import com.mochat.rabbitmq.RabbitRandomPublish;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.redis.RedisLock;
import com.mochat.service.ConnectionInterface;
import com.mochat.threadPool.RandomThreadPool;
import com.mochat.websocket.MoChatSessionManager;
import com.util.IpUtils;
import com.util.UUIDGenerator;

import redis.clients.jedis.JedisCluster;

@Service
public class RandomChatService  implements ConnectionInterface{

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomThreadPool.class);//Logger.getLogger(RandomThreadPool.class);

	//private static final Logger LOGGER = Logger.getLogger(RandomChatService.class);
	@Autowired
	private CustomRedisClusters customRedisCluster;

	@Autowired
	private RabbitRandomPublish rabbitRandomPublish;
	
	@Autowired
	private RedisConstants redisConstants;
	private static final long expose_time = 1000000;
	
	
	public Boolean checkUser(HttpServletRequest request) {
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		
		//jedisCluster.s
		return true;
	}
	
	/**
	 * @param request
	 * @return
	 */
	@Override
	public UserInfo connectUser(HttpServletRequest request) {
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		UserInfo currentUserInfo = ThreadLocalCache.get();
		String userId = currentUserInfo.getUserId();
		HttpSession session = request.getSession(true);
		LOGGER.info("用户："+userId+"开始创建连接");
		String toUserId = null;
		String requestId = UUIDGenerator.generate();
		boolean flag = RedisLock.tryGetDistributedLock(jedisCluster, RedisConstants.CONNECT_KEY, requestId, 500);
		currentUserInfo.setUserId(userId);
			try {
				if(flag == true) {
				 toUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
					if(toUserId != null) {
						currentUserInfo.setUserId(userId);
						currentUserInfo.setToUserId(toUserId);
						WebSocketSession webSocketSession = MoChatSessionManager.SESSION_MAP.get(userId);
						if(webSocketSession != null) {
							currentUserInfo.setFirstConnect(false);
						}
						jedisCluster.srem(RedisConstants.WAIT_SET, toUserId);
						jedisCluster.srem(RedisConstants.WAIT_SET, userId);
						jedisCluster.hset(RedisConstants.CHAT_ING_HASH, userId, toUserId);
						jedisCluster.hset(RedisConstants.CHAT_ING_HASH, toUserId, userId);
						//return currentUserInfo;
					}else {
						long waitLength = jedisCluster.scard(RedisConstants.WAIT_SET);
						if(waitLength == 0) {
							jedisCluster.sadd(RedisConstants.WAIT_SET, userId);
						}
						if (waitLength == 1) {
							toUserId = jedisCluster.srandmember(RedisConstants.WAIT_SET);
							if(StringUtils.equals(toUserId, userId)) {
								toUserId = null;
							}
						}
						if(waitLength > 1) {
							toUserId = jedisCluster.srandmember(RedisConstants.WAIT_SET);
							if(StringUtils.equals(toUserId, userId)) {
								jedisCluster.srem(RedisConstants.WAIT_SET, toUserId);
							}
							toUserId = jedisCluster.srandmember(RedisConstants.WAIT_SET);
						}
						if(toUserId != null&&!StringUtils.equals(userId, toUserId)){
							jedisCluster.srem(RedisConstants.WAIT_SET, userId);
							jedisCluster.srem(RedisConstants.WAIT_SET, toUserId);
							jedisCluster.hset(RedisConstants.CHAT_ING_HASH, userId, toUserId);
							jedisCluster.hset(RedisConstants.CHAT_ING_HASH, toUserId, userId);
							currentUserInfo.setUserId(userId);
							currentUserInfo.setToUserId(toUserId);
							
							RandomMessageBody msBody = new RandomMessageBody();
							msBody.setFirstConnect(true);
							msBody.setToUserId(toUserId);
							msBody.setUserId(userId);
							String str = JSONObject.toJSON(msBody).toString();
							LOGGER.info("创建连接成功："+str);
							//jedisCluster.publish(RedisConstants.SUB_PUB_CHANNEL, str);
							rabbitRandomPublish.send("R",str);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage(), e);
				currentUserInfo.setToUserId(null);
				return currentUserInfo;
			}finally {
				currentUserInfo.setToUserId(toUserId);
				ThreadLocalCache.set(currentUserInfo);
				session.setAttribute(Constants.USER_SESSION, currentUserInfo);
				RedisLock.releaseDistributedLock(jedisCluster, RedisConstants.CONNECT_KEY, requestId);
			}
			
			return currentUserInfo;
	}

	public RandomMessageBody sendMsg(HttpServletRequest request) throws MoChatException, Exception{

        UserInfo userInfo = ThreadLocalCache.get();
        String userId = userInfo.getUserId();
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		RandomMessageBody msBody = new RandomMessageBody();
		String redisToUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
		if(userId==null||
				redisToUserId==null) {
			msBody.setDisConnectFlag(true);
 		}
		return msBody;
	}
	@Override
	public RandomMessageBody disConnection(HttpServletRequest request) throws Exception{
		// TODO Auto-generated method stub
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		String userId = request.getParameter("userId");
		HttpSession session = request.getSession(true);
		Object object = session.getAttribute(Constants.USER_SESSION);
		UserInfo currentUserInfo = null;
		
		if(object == null) {
			if(object instanceof UserInfo) {
				currentUserInfo = (UserInfo)object;
				userId = currentUserInfo.getUserId();
			}
		}
		
		String toUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
		return disconnect(jedisCluster, userId, toUserId);
		
	}
	
	private final RandomMessageBody disconnect(JedisCluster jedisCluster,String userId,String toUserId)throws Exception {
		RandomMessageBody msBody = new RandomMessageBody();
		msBody.setUserId(userId);
		msBody.setToUserId(toUserId);
		msBody.setDisConnectFlag(true);
		if(userId != null) {
			jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, userId);
		}
		if(toUserId != null) {
			jedisCluster.hdel(RedisConstants.CHAT_ING_HASH, toUserId);
		}
		if(userId != null &&toUserId != null) {
			rabbitRandomPublish.send("R",JSONObject.toJSON(msBody).toString());
		}
		
		return msBody;
	}
	
	public Map<String, Long> getAllUserInfo(HttpServletRequest request){
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		UserInfo currentUserInfo = ThreadLocalCache.get();
		if(currentUserInfo == null) {
			request.getSession(true);
			String ip = IpUtils.getIpAddress(request);
			jedisCluster.sadd(RedisConstants.ON_LINE_SET, ip);
			currentUserInfo = new UserInfo();
			currentUserInfo.setUserId(ip);
			ThreadLocalCache.set(currentUserInfo);
		}
		long onLine = jedisCluster.scard(RedisConstants.ON_LINE_SET);
		long chatIng = jedisCluster.hlen(RedisConstants.CHAT_ING_HASH);
		
		Map<String, Long> map = new HashMap<String,Long>();
		
		map.put("onLine", onLine);
		map.put("chat", chatIng);
		return map;
		
	}
}
