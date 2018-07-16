package com.mochat.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import com.mochat.cache.ThreadLocalCache;
import com.mochat.constant.Constants;
import com.mochat.model.UserInfo;

/**
 */
@Component
public class MoHandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {
    //初次握手访问前
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        
    	if (request instanceof ServletServerHttpRequest) {
        	if(request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {  
                request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");  
            }  
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest request2 = servletRequest.getServletRequest();
           
            HttpSession session = request2.getSession(true);
            UserInfo userInfo = ThreadLocalCache.get(); //如果没有缓存则
            if(userInfo == null) {
            	return false;
            }
            String chatType = request2.getParameter("type");
            if(chatType == null) {
            	return false;
            }
            userInfo.setChatType(chatType);
            if(StringUtils.equalsIgnoreCase(chatType, Constants.RANDOM_CHAT)) {//如果是随机聊天
            	 WebSocketSession webSocketSession = MoChatSessionManager.SESSION_MAP.get(userInfo.getUserId());
                 if(webSocketSession != null&&webSocketSession.isOpen()) {
                 	return false;
                 }
            }
            if(StringUtils.equalsIgnoreCase(chatType, Constants.ROOM_CHAT)) {
            	
            }
            
            /*if (session != null) {
                Object temp = session.getAttribute(Constants.USER_SESSION);
                if(temp != null) {//如果存在session,则把session中的用户信息存放在threadlocalcache中
                	if(temp instanceof UserInfo) {
                		UserInfo currentUserInfo = (UserInfo)temp;
            			ThreadLocalCache.set(currentUserInfo);
                	}
                }else {//如果不存在session则把信息存储在session中
                	String userId = request2.getParameter("userId");
                	UserInfo currentUserInfo = new UserInfo();
                	currentUserInfo.setUserId(userId);
                	ThreadLocalCache.set(currentUserInfo);
                	session.setAttribute(Constants.USER_SESSION, currentUserInfo);
                }
            }*/
        }
        return true;
    }

    //初次握手访问后
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        System.err.println("有人访问了：------------" + serverHttpRequest.getRemoteAddress());
    }
}