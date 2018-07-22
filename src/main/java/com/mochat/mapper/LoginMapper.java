package com.mochat.mapper;

import java.util.List;

import com.mochat.model.ChatRoom;
import com.mochat.model.UserInfo;

public interface LoginMapper {

	public List<UserInfo> getUserInfo(UserInfo userInfo);
	
	public void userRegister(UserInfo userInfo);
	
	public void createChatRoom(ChatRoom chatRoom);
	
	public void deleteChatRoom(String roomId);
}
