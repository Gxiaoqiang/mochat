package com.mochat.model;

import java.util.List;

/**
 * 响应数据
 * 
 * @author 80274996
 * 
 * @param <T>
 */
public class ResponseEntity<T> {
	private Boolean success;
	private T data;
	private String msg;
	private Integer start;
	private Integer limit;
	private Long total;

	public ResponseEntity() {
		super();
	}

	public ResponseEntity(Boolean success, T data, String msg, Integer start, Integer limit, Long total) {
		super();
		this.success = success;
		this.data = data;
		this.msg = msg;
		this.start = start;
		this.limit = limit;
		this.total = total;
	}

	public ResponseEntity(Boolean success, T data, String msg) {
		super();
		if (data instanceof List) {
			this.start = 0;
			this.limit = ((List<?>) data).size();
			this.total = Long.valueOf(this.limit);
		}
		this.success = success;
		this.data = data;
		this.msg = msg;
	}
	
	public ResponseEntity(T data){
		super();
		if (data instanceof List) {
			this.start = 0;
			this.limit = ((List<?>) data).size();
			this.total = Long.valueOf(this.limit);
		}
		this.success = true;
		this.data = data;
		this.msg = "操作成功";
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
