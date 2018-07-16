package com.mochat.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

public class MoChatSessionManager {
	/**
	 * 存放用户唯一性id，和用户连接时的session
	 */
	public static final ConcurrentHashMap<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<String,WebSocketSession>();
	
	public static final ConcurrentHashMap<WebSocketSession, String> SESSION_ID_MAP = new ConcurrentHashMap<WebSocketSession, String>();
	
}
