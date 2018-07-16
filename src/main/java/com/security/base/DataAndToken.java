package com.security.base;

/**
 * data和token 类
 *
 */
public class DataAndToken {
	
	private String data;
	
	private String token;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "[data=" + data + ", token=" + token + "]";
	}
	
}
