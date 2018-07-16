package com.mochat.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mochat.model.RandomMessageBody;
import com.mochat.model.ResponseEntity;
import com.mochat.model.UserInfo;
import com.mochat.service.impl.RandomChatService;

@Controller
@RequestMapping("/connection")
public class RandomChatController extends CommonHandler{

	@Autowired
	private RandomChatService connectionService;
	
	@ResponseBody
	@RequestMapping("/find")
	public ResponseEntity<UserInfo> connection(HttpServletRequest request){
		UserInfo currentUserInfo = connectionService.connectUser(request);
		return successHandle(true, currentUserInfo, null);
	}
	
	@ResponseBody
	@RequestMapping("/sendMsg")
	public ResponseEntity<RandomMessageBody> sendMsg(HttpServletRequest request)throws Exception{
		
		RandomMessageBody messageBody = connectionService.sendMsg(request);
		
		return successHandle(true, messageBody, null);
	}
	/**
	 * 断开连接
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/disconnect")
	public ResponseEntity<RandomMessageBody> disConnection(HttpServletRequest request)throws Exception{
		RandomMessageBody msBody = connectionService.disConnection(request);
		return successHandle(true, msBody, null);
	}
	
	@ResponseBody
	@RequestMapping("/checkUser")
	public ResponseEntity<Boolean> checkUser(HttpServletRequest request){
		return null;
	}
	
	@ResponseBody
	@RequestMapping("/getAllUserInfo")
	public ResponseEntity<Map<String, Long>> getAllUserInfo(HttpServletRequest request){
		return successHandle(true, connectionService.getAllUserInfo(request), null);
	}
}
