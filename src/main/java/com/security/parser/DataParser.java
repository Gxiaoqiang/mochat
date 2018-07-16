package com.security.parser;

public interface DataParser {
	
	/**
	 * 解析数据实体
	 * @param data
	 * @return
	 */
	Object parseDataEntity(String data) throws SecurityException;
	
	/**
	 * 解析时间
	 * @param data
	 * @return
	 */
	Long parseTime(String data) throws SecurityException;

}
