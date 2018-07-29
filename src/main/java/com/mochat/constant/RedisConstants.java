package com.mochat.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedisConstants {
	
	
    @Value("${redis.nodes:null}")
    private String redisNodes;

    @Value("${redis.password:null}")
    private String Password;

    @Value("${switch:Y}")
    private String switchButton;
    
    
    
    
	public String getSwitchButton() {
		return switchButton;
	}

	public void setSwitchButton(String switchButton) {
		this.switchButton = switchButton;
	}

	public String getRedisNodes() {
		return redisNodes;
	}

	public void setRedisNodes(String redisNodes) {
		this.redisNodes = redisNodes;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public static final String WAIT_SET = "wait_set_23";//等待数组,当只有一个用户的时候,会将用户放在等待SET中,等待其他用户连接该用户
	

	public static final String CHAT_ING_HASH = "chat_ing_hash_23";//正在聊天中的两个人,用hash表示,谁建立连接用谁的ip做为key,聊天对象的ip作为value
   
	
	public static final String ON_LINE_SET = "on_line_set_23"; //当前在线人数
	
  
	public static final String SUB_PUB_CHANNEL = "sub_pub_channel";//订阅发布队列，负责发布和监听

	
	public static final String CONNECT_KEY = "connect_key";
	
	public static final String USER_INFO_SUFFIX = "_userInfo";
	
	public static final String VERIFICATION = "_verification";
	
	public static final Integer EXPIRE_TIME = 2*60*60;
	
	public static final String LOGIN_FLAG = "_loginFlag";
	/**
	 * 聊天类型,
	 * 1:随机,2:聊天室
	 * @author 80374514
	 *
	 */
	public static enum ChatType{
		RANDOM("1"),ROOM("2");
		
		private String chatType;
		
		private ChatType(String type) {
			this.chatType = type;
		}
		
		public String getChatType() {
			return this.chatType;
		}
	}
}
