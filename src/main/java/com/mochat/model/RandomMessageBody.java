package com.mochat.model;

import java.io.Serializable;
import java.util.Date;

public class RandomMessageBody implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1646760390836350300L;
	
	private String msgId;
	
	private String userId;
	
	private String toUserId;
	
	private String msg;
	
	private Date date;

	private String msgDate;
	
	private Boolean flag;//是否报错
	
	private Boolean disConnectFlag = false;
	
	private Boolean firstConnect = false; //初次连接标示，标示你已经被对方连接上了
	public Boolean getFirstConnect() {
		return firstConnect;
	}

	public void setFirstConnect(Boolean firstConnect) {
		this.firstConnect = firstConnect;
	}

	public String getMsgDate() {
		return msgDate;
	}

	public void setMsgDate(String msgDate) {
		this.msgDate = msgDate;
	}

	public Boolean getDisConnectFlag() {
		return disConnectFlag;
	}

	public void setDisConnectFlag(Boolean disConnectFlag) {
		this.disConnectFlag = disConnectFlag;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

    
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}


	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		return "{\"msgId\":\""+msgId+"\",\"userId\":\""+userId+"\",\"toUserId\":\""+toUserId+"\",\"msgContent\":\""+msg
				+"\",\"msgDate\":\""+msgDate+"\",\"flat\":\""+flag+"\",\"disConnectFlag\":\""+disConnectFlag+"\"}";
	}

	
}
