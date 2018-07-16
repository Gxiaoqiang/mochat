package com.security.factory;

import org.apache.log4j.Logger;

import com.security.verify.SecurityVerify;

/**
 * 安全认证类
 *
 */
public class SecurityVerifyFactory {
	
	private SecurityVerify securityVerify;
	
	private static final Logger LOGGER = Logger.getLogger(SecurityVerifyFactory.class);

	public SecurityVerify getSecurityVerify() {
		return securityVerify;
	}

	public void setSecurityVerify(SecurityVerify securityVerify) {
		this.securityVerify = securityVerify;
	}
	
	/**
	 * 验证data和token
	 * @param data
	 * @param token
	 * @return
	 */
	public boolean verifyDataToken(String data, String token) {
			try {
				if (securityVerify.verify(data, token)) {
					return true;
				}
			} catch (SecurityException e) {
				LOGGER.error("verify data and token failed!验证data和token失败!", e);
			}
		return false;
		
	}
	
	/**
	 * 根据数据对象
	 * @param data
	 * @return
	 */
	public Object getDataEntity(String data) {
		Object dataEntity = null;
			try {
				dataEntity = securityVerify.getDataEntity(data);
				if (dataEntity != null) {
					return dataEntity;
				}
			} catch (SecurityException e) {
				LOGGER.error("obtain dataEntity failed! 获取数据对象失败!", e);
			}
		return null;
	}
	
}
