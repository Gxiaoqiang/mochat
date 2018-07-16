package com.mochat.model;

import java.io.Serializable;

public class UserQuestion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userMail;
	
	private String userId;
	
	private String questionId;
	
	private String questionResult;

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getQuestionResult() {
		return questionResult;
	}

	public void setQuestionResult(String questionResult) {
		this.questionResult = questionResult;
	}
	
	

}
