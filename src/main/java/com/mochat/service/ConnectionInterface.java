package com.mochat.service;

import javax.servlet.http.HttpServletRequest;

import com.mochat.exception.MoChatException;
import com.mochat.model.UserInfo;
import com.mochat.model.RandomMessageBody;

public interface ConnectionInterface {

	/**
	 * 用户获取连接
	 * @param request
	 * @return
	 */
	UserInfo connectUser(HttpServletRequest request);
	
	/**
	 * 用户断开连接
	 * @param request
	 * @return
	 */
	RandomMessageBody disConnection()throws Exception;
	
	
}
