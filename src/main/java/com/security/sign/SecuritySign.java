package com.security.sign;

import com.security.base.DataAndToken;
import com.security.builder.DataBuilder;

/**
 * 安全加密类
 * @author 80374523
 *
 */
public interface SecuritySign {
	
	/**
	 * 获取加密策略名称
	 * @return
	 */
	String getStrategyName();
	
	/**
	 * 加密需要加密的密文，并返回机密后的密文(token)
	 * @param data
	 * @return
	 * @throws SecurityException
	 */
	String sign(String data) throws SecurityException;
	
	/**
	 * 加密需要加密的密文(由对象)，并返回机密后的密文和编码后的原文(data & token)
	 * @param entity Object
	 * @return
	 * @throws SecurityException
	 */
	DataAndToken buildDataAndToken(Object entity) throws SecurityException;
	
	/**
	 * 设置data构造器
	 * @param dataBuiler
	 */
	void setDataBuilder(DataBuilder dataBuiler);

}
