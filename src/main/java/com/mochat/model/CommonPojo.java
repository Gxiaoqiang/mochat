package com.mochat.model;

import java.io.Serializable;
import java.util.Date;

public class CommonPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8734946150624975249L;

	
	private Date createTime;
	
	private Date updTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdTime() {
		return updTime;
	}

	public void setUpdTime(Date updTime) {
		this.updTime = updTime;
	}
	
	
}
