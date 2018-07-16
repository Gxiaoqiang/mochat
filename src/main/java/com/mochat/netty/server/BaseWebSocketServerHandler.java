package com.mochat.netty.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.mochat.cache.ThreadLocalCache;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.listener.MyApplicationContextListener;
import com.mochat.model.RandomMessageBody;
import com.mochat.model.RoomMessageBody;
import com.mochat.model.UserInfo;
import com.mochat.netty.constant.NettyContextManage;
import com.mochat.netty.exception.NettyException;
import com.mochat.rabbitmq.RabbitRandomPublish;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.websocket.MoChatSessionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import redis.clients.jedis.JedisCluster;


/**
 * 发消息方式 抽象出来
 * 
 * */
public abstract class BaseWebSocketServerHandler extends SimpleChannelInboundHandler<Object>{

	private static final Logger LOGGER = Logger.getLogger(BaseWebSocketServerHandler.class);
    
    /**
     * 推送单个
     * 
     * */
    public static final void pushRandom(final ChannelHandlerContext ctx,final String message){
        
        TextWebSocketFrame tws = new TextWebSocketFrame(message);
        ctx.writeAndFlush(tws);
    }
    
    /**
     * 群发
     * @param messageBody
     */
    public static final void roomPush(final RoomMessageBody messageBody) {
    	String roomId = messageBody.getRoomId();
    	//ChannelGroup chinnelGroup = NettyContextManage.CHANNELGROUP_MAP.get(roomId);
    	String message = JSONObject.toJSONString(messageBody);
    	//TextWebSocketFrame tws = new TextWebSocketFrame(message);
    	/*if(chinnelGroup != null) {
    		 chinnelGroup.write(tws);
    	}*/
		RabbitRandomPublish rabbitRandomPublish = (RabbitRandomPublish) MyApplicationContextListener.getBean("rabbitRandomPublish");

		rabbitRandomPublish.send("M", message);
    }
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	protected void addRoomChannel(ChannelHandlerContext ctx,UserInfo userInfo)throws Exception{
		CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
 	    String roomId = jedisCluster.get(userInfo.getUserId()+Constants.ROOM_CHAT);
 	    ChannelGroup channelGroup = NettyContextManage.CHANNELGROUP_MAP.get(roomId);
 	    if(channelGroup == null) {
 	    	channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
 	    }
 	    channelGroup.add(ctx.channel());
 	   NettyContextManage.CHANNELGROUP_MAP.put(roomId, channelGroup);
	}
	
	protected void addRandomChannel(ChannelHandlerContext ctx,UserInfo userInfo)throws Exception {
		try {
			/*CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();*/
			UserInfo currentUserInfo = ThreadLocalCache.get();
			String ip = ctx.channel().remoteAddress().toString();
			String userId = currentUserInfo.getUserId();
			ChannelHandlerContext ctx2 = null;
			if(userId != null) {
				ctx2 = NettyContextManage.ID_CTX_MAP.get(userId);
			}
			if(ctx2 != null&&ctx2.channel().isActive()) {
				throw new NettyException("no repeat login!");
			}
			if(userId != null) {
				NettyContextManage.ID_CTX_MAP.put(userId, ctx);
				NettyContextManage.CTX_ID_MAP.put(ctx, userId);
			}
			LOGGER.info("用户+"+ip+"已经接入");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	
	protected void closeChannelContext(ChannelHandlerContext ctx)throws Exception{
		UserInfo userInfo = ThreadLocalCache.get();
		if(userInfo == null) {
			return;
		}
		String chatType = userInfo.getChatType();
		if(StringUtils.equals(chatType, Constants.RANDOM_CHAT)) {
			closeRandomContext(ctx, userInfo);
		}
		if(StringUtils.equals(chatType, Constants.ROOM_CHAT)) {
			closeRoomContext(ctx, userInfo);
		}
	}
	/**
	 * 发送room聊天
	 * @param jedisCluster
	 * @param userInfo
	 * @param request
	 */
	protected void roomChatPush(JedisCluster jedisCluster,UserInfo userInfo,String request) {
		    String userId = userInfo.getUserId();
	    	String roomId = jedisCluster.get(userId+Constants.ROOM_CHAT);
	    	if(roomId == null) {
	    		return;
	    	}
	    	JSONObject jsonObject = JSONObject.parseObject(request);
	    	RoomMessageBody roomMessageBody = JSONObject.toJavaObject(jsonObject, RoomMessageBody.class);//new RoomMessageBody();
	    	String userInfoStr = jedisCluster.get(userId+RedisConstants.USER_INFO_SUFFIX);
	    	roomMessageBody.setRoomId(roomId);
	    	roomMessageBody.setUserId(userInfo.getUserId());
	    	roomMessageBody.setUserInfo(userInfoStr);
	    	BaseWebSocketServerHandler.roomPush(roomMessageBody);
	}
	/**
	 * 发送随机聊天
	 * @param jedisCluster
	 * @param userInfo
	 * @param request
	 */
	protected void randomChatPush(ChannelHandlerContext ctx,JedisCluster jedisCluster,UserInfo userInfo,String request) {
		JSONObject jsonObject = JSONObject.parseObject(request);
		RabbitRandomPublish rabbitRandomPublish = (RabbitRandomPublish) MyApplicationContextListener.getBean("rabbitRandomPublish");
		String userId = jsonObject.getString("userId");
		String redisToUserId = jedisCluster.hget(RedisConstants.CHAT_ING_HASH, userId);
		if(redisToUserId == null) {
			jsonObject.put("disConnectFlag", true);
			ctx.writeAndFlush(new TextWebSocketFrame(jsonObject.toJSONString()));
			return;
		}
		jsonObject.put("toUserId", redisToUserId);
		ChannelHandlerContext toContext = NettyContextManage.ID_CTX_MAP.get(redisToUserId);
		if(toContext != null) {
			toContext.writeAndFlush(new TextWebSocketFrame(jsonObject.toJSONString()));
			return;
		}
		rabbitRandomPublish.send("R",jsonObject.toString());
	}
	private void closeRoomContext(ChannelHandlerContext ctx,UserInfo userInfo)throws Exception{
		CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
 	    String roomId = jedisCluster.get(userInfo.getUserId()+Constants.ROOM_CHAT);
 	    if(roomId == null) {
 	    	return;
 	    }
 	    ChannelGroup channelGroup = NettyContextManage.CHANNELGROUP_MAP.get(roomId);
 	    if(channelGroup != null) {
 		    channelGroup.remove(ctx.channel());
 	    }
 	    if(channelGroup.isEmpty()) {
 	    	NettyContextManage.CHANNELGROUP_MAP.remove(roomId);
 	    }
 	    jedisCluster.del(userInfo.getUserId()+Constants.ROOM_CHAT);
 	    jedisCluster.srem(roomId+Constants.ROOM_CHAT,userInfo.getUserId());
 	    ctx.close();
	}
	private void closeRandomContext(ChannelHandlerContext ctx,UserInfo userInfo)throws Exception{
		String userId = null;
		try {
			if(userInfo != null) {
				 userId = userInfo.getUserId();
			}else {
				userId = NettyContextManage.CTX_ID_MAP.get(ctx);
			}
			if(userId == null) {
				userId = NettyContextManage.CTX_ID_MAP.get(ctx);
			}
			RabbitRandomPublish rabbitRandomPublish = (RabbitRandomPublish) MyApplicationContextListener.getBean("rabbitRandomPublish");
			CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
			String toUserId = null;
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
	 	    }
	 	    if(userId!=null&&toUserId!=null) {
	 	    	RandomMessageBody msBody = new RandomMessageBody();
				msBody.setUserId(userId);
				msBody.setToUserId(toUserId);
				msBody.setDisConnectFlag(true);
				rabbitRandomPublish.send("R",JSONObject.toJSON(msBody).toString());
	 	    }
	 	    ctx.close();
	 	    //ctx.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		} finally {
			ThreadLocalCache.remove();
			NettyContextManage.CTX_ID_MAP.remove(ctx);
			if(userId != null) {
		 	    NettyContextManage.ID_CTX_MAP.remove(userId);	
			}
		}
	}
	
}