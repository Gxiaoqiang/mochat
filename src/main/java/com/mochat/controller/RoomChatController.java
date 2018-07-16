package com.mochat.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mochat.cache.ThreadLocalCache;
import com.mochat.model.ResponseEntity;
import com.mochat.model.UserInfo;
import com.mochat.service.impl.RoomChatService;

@Controller
@RequestMapping("/roomChat")
public class RoomChatController extends CommonHandler{

	@Autowired
	private RoomChatService roomChatService;
	
	
	@ResponseBody
	@RequestMapping("/joinRoom")
	public ResponseEntity<List<UserInfo>> joinRoom(HttpServletRequest request,@RequestParam("roomId")String roomId) throws Exception{
		UserInfo userInfo = ThreadLocalCache.get();
		List<UserInfo> userInfos = roomChatService.joinRoom(roomId, userInfo.getUserId());
		return successHandle(true, userInfos, null);
	}
	
	public ResponseEntity<Boolean> checkJoin()throws Exception{
		return successHandle(true, roomChatService.checkJoinRoom(), null);
	}
	
	@ResponseBody
	@RequestMapping("/quitRoom")
	public ResponseEntity<Boolean> quitRoom()throws Exception{
		roomChatService.quitRoom();
		return successHandle(true, true, null);
	}
	@ResponseBody
	@RequestMapping("/getOnLine")
	public ResponseEntity<Long> getOnLine(){
		
		Long num = roomChatService.getOnLine();
		return successHandle(true, num, null);
	}
	
	@RequestMapping("/{roomId}/getChannelSize")
	@ResponseBody
	public ResponseEntity<Integer> getChannelSize(@PathVariable("roomId")String roomId){
		Integer num = roomChatService.getChannelGroupSize(roomId);
		return successHandle(true, num, null);
	}
}
