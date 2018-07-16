package com.mochat.model;

import java.io.Serializable;

public class ChatRoom  extends CommonPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8015631090173400930L;
	
	private String roomId;
	
	private String roomName;
	
	private String roomDesc;
	
	private String userId;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomDesc() {
		return roomDesc;
	}

	public void setRoomDesc(String roomDesc) {
		this.roomDesc = roomDesc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
