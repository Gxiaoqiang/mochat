package com.mochat.model;

import java.io.Serializable;

/**
 * 群聊的消息体
 * @author 80374514
 *
 */
public class RoomMessageBody implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6269148470540205053L;
	
	
	private int joinFlag = 0; //1表示加入，2表示离开
	private Boolean quitFlag = false; //是否是退出
	
	private String userId;//用户Id
	
	private String userNickName;//用户绰号
	
	
	
	private String roomId;//发送的roomId

	private String msgContent;//消息体

    private String  userInfo;
    
    
	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public int getJoinFlag() {
		return joinFlag;
	}

	public void setJoinFlag(int joinFlag) {
		this.joinFlag = joinFlag;
	}

	public Boolean getQuitFlag() {
		return quitFlag;
	}

	public void setQuitFlag(Boolean quitFlag) {
		this.quitFlag = quitFlag;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	
	
	
}
