package com.mochat.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户的连接信息
 * @author WQG
 *
 */
public class UserInfo extends CommonPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2528361055308950334L;
	private String port;
	private String userId;
	private String toUserId;
    private String email;

    private String nickname;

    private String userPassWord;

    private String icons;

    private Integer userType;

    private String loginTime; //登录时间，用来判断data和token是否过期
    
    private String ip; //当前登录ip
    
    private String userPhone;
    
    private boolean firstConnect = true;
    
    private String chatType; //聊天类型，random，room
    
    private String roomId;
    
    private String sex;
    
    private String userStatus;
    
    private String flag;//判断单点登录，标识，为单一随机数
    
    public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	private String srand;
	
	private String rePassWord;
	
	private String vCode;

	public String getSrand() {
		return srand;
	}

	public void setSrand(String srand) {
		this.srand = srand;
	}

	public String getRePassWord() {
		return rePassWord;
	}

	public void setRePassWord(String rePassWord) {
		this.rePassWord = rePassWord;
	}

	public String getvCode() {
		return vCode;
	}

	public void setvCode(String vCode) {
		this.vCode = vCode;
	}
	


	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getChatType() {
		return chatType;
	}

	public void setChatType(String chatType) {
		this.chatType = chatType;
	}

	public boolean isFirstConnect() {
		return firstConnect;
	}

	public void setFirstConnect(boolean firstConnect) {
		this.firstConnect = firstConnect;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getUserPassWord() {
		return userPassWord;
	}

	public void setUserPassWord(String userPassWord) {
		this.userPassWord = userPassWord;
	}

	public String getIcons() {
		return icons;
	}

	public void setIcons(String icons) {
		this.icons = icons;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
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

	private String userName;


	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
