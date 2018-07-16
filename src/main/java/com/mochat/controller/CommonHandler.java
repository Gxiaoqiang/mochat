/**
 * 
 */
package com.mochat.controller;

import com.mochat.model.ResponseEntity;

/**
 * @author 80374514
 *
 */
public class CommonHandler {
	
	public <T> ResponseEntity<T> successHandle(boolean flag,T t,String msg){
		return new ResponseEntity<T>(flag, t, msg);
	}
	
	public <T> ResponseEntity<T> successHandle(boolean flag, T t, String msg, long total){
		ResponseEntity<T> responseEntity = this.successHandle(flag, t, msg);
		responseEntity.setTotal(total);
		return responseEntity;
	}
}
