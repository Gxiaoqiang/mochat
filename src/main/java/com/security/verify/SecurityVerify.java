package com.security.verify;

import com.security.parser.DataParser;

/**
 * 认证接口
 * @author 80374523
 *
 */
public interface SecurityVerify {
	
	/**
	 * 认证策略名称
	 * @return
	 */
	String getVerifyStrategyName();
	
	/**
	 * 认证data和token
	 * @param data
	 * @param token
	 * @return
	 * @throws SecurityException
	 */
	boolean verify(String data, String token) throws SecurityException;
	
	/**
	 * 根据密文data获取数据对象
	 * @param data
	 * @return
	 * @throws SecurityException
	 */
	Object getDataEntity(String data) throws SecurityException;
	
	/**
	 * 设置具体的data解析
	 * @param dataParser
	 */
	void setDataParser(DataParser dataParser);

}
