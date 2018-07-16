package com.mochat.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户的连接信息
 * @author WQG
 *
 */
public class CurrentUserInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2528361055308950334L;
	private String port;
	
	
	private String userId;
	
	private String toUserId;
	
    /** 邮箱 */
    private String email;

    /** 昵称
             */
    private String nickname;

    /** 密码 */
    private String password;

    /** 头像 */
    private String icons;

    /** 在线状态 */
    private Integer onLineStatus;

    private Integer userType;

    private Long createById;

    private Date createByDate;

    private Long updateById;

    private Date updateByDate;
	
    
    
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIcons() {
		return icons;
	}

	public void setIcons(String icons) {
		this.icons = icons;
	}

	public Integer getOnLineStatus() {
		return onLineStatus;
	}

	public void setOnLineStatus(Integer onLineStatus) {
		this.onLineStatus = onLineStatus;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Long getCreateById() {
		return createById;
	}

	public void setCreateById(Long createById) {
		this.createById = createById;
	}

	public Date getCreateByDate() {
		return createByDate;
	}

	public void setCreateByDate(Date createByDate) {
		this.createByDate = createByDate;
	}

	public Long getUpdateById() {
		return updateById;
	}

	public void setUpdateById(Long updateById) {
		this.updateById = updateById;
	}

	public Date getUpdateByDate() {
		return updateByDate;
	}

	public void setUpdateByDate(Date updateByDate) {
		this.updateByDate = updateByDate;
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
