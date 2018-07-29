package com.mochat.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mochat.cache.ThreadLocalCache;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.model.ChatRoom;
import com.mochat.model.RoomMessageBody;
import com.mochat.model.UserInfo;
import com.mochat.netty.constant.NettyContextManage;
import com.mochat.rabbitmq.RabbitRandomPublish;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.threadPool.RandomThreadPool;

import io.netty.channel.group.ChannelGroup;
import redis.clients.jedis.JedisCluster;

@Service
public class RoomChatService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomThreadPool.class);//Logger.getLogger(RandomThreadPool.class);

	//private static final Logger LOGGER = Logger.getLogger(RoomChatService.class);
	@Autowired
	private CustomRedisClusters customRedisCluster;

	@Autowired
	private RabbitRandomPublish rabbitRandomPublish;
	
	/**
	 * 获取所有聊天室信息
	 * @return
	 * @throws Exception
	 */
	public List<ChatRoom> getAllRoom()throws Exception{
	
		return null;
	}
	
	public Integer getChannelGroupSize(String roomId) {
		ChannelGroup channelGroup = NettyContextManage.CHANNELGROUP_MAP.get(roomId);
		if(channelGroup != null) {
			return channelGroup.size();
		}
		return 0;
	}
	
	public Long getOnLine() {
		UserInfo userInfo = ThreadLocalCache.get();
		String userId = userInfo.getUserId();
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		String roomId = jedisCluster.get(userId+Constants.ROOM_CHAT);
		return jedisCluster.scard(roomId+Constants.ROOM_CHAT);
	}
	
	public boolean quitRoom(){
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		UserInfo userInfo = ThreadLocalCache.get();
		String userId = userInfo.getUserId();
		
		String roomId = jedisCluster.get(userId+Constants.ROOM_CHAT);
		jedisCluster.del(userId+Constants.ROOM_CHAT);
		if(roomId != null) {
			jedisCluster.srem(roomId+Constants.ROOM_CHAT, userId);
			System.out.println("用户退出聊天室+*****************");
		}
		return true;
	}
	
	public Boolean checkJoinRoom()throws Exception{
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		UserInfo userInfo = ThreadLocalCache.get();
		String roomIdTemp = jedisCluster.get(userInfo.getUserId()+"_"+Constants.ROOM_CHAT);
		if(roomIdTemp != null) {//如果已经加入一个聊天室，则不能再加入新的
			throw new Exception("已经正在一个聊天室中，请先退出！");
		}
		return true;
	}
	
	/**
	 * 加入成功返回聊天室人的信息,聊天室最多600人
	 * @param roomId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<UserInfo> joinRoom(String roomId,String userId)throws Exception{
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		UserInfo userInfo = ThreadLocalCache.get();
		if(!StringUtils.equals(userId, userInfo.getUserId())) {
			throw new Exception("所传的数据错误!");
		}
		String roomIdTemp = jedisCluster.get(userId+Constants.ROOM_CHAT);
		if(roomIdTemp != null) {//如果已经加入一个聊天室，则不能再加入新的
			throw new Exception("已经正在一个聊天室中，请先退出！");
		}
		LOGGER.info("用户"+ userInfo.getNickname()+"加入聊天室"+roomId);
		jedisCluster.set(userId+Constants.ROOM_CHAT, roomId);
		jedisCluster.sadd(roomId+Constants.ROOM_CHAT, userId);//加入到群的id中，为set的数组
		
		 Set<String> userIds = jedisCluster.smembers(roomId+Constants.ROOM_CHAT);
		 
		 userInfos.add(userInfo);
		 for(String uId :userIds) {
			 String userStr = jedisCluster.get(uId+RedisConstants.USER_INFO_SUFFIX);
			 JSONObject jsonObject = JSONObject.parseObject(userStr);
			 UserInfo uInfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);
			 userInfos.add(uInfo);
		 }
		
		RoomMessageBody roomMessageBody = new RoomMessageBody();
		roomMessageBody.setJoinFlag(1);//加入标志
		
		roomMessageBody.setMsgContent(JSONObject.toJSONString(userInfo).toString());//
		roomMessageBody.setRoomId(roomId);
		
		rabbitRandomPublish.send("M", JSONObject.toJSONString(roomMessageBody).toString()); //发送消息
		return userInfos;
	}
}
