package com.security.factory;

import java.net.URI;

import org.apache.log4j.Logger;

import com.security.base.DataAndToken;
import com.security.constants.SecurityConstants;
import com.security.sign.SecuritySign;

/**
 * 安全签名工厂类
 * @author 80374523
 *
 */
public class SecuritySignFactory {
	
	private static final String AND_STR = "&";
	private static final String EQUALS = "=";
	
	private static final Logger LOGGER = Logger.getLogger(SecuritySignFactory.class);
	
	/**
	 * 安全签名策略
	 */
	private SecuritySign securitySign;

	public SecuritySign getSecuritySign() {
		return securitySign;
	}

	public void setSecuritySign(SecuritySign securitySign) {
		this.securitySign = securitySign;
	}
	
	public SecuritySign getInstance() {
		return securitySign;
	}
	
	/**
	 * 根据数据对象实体构建data和token
	 * @param entity Object
	 * @return
	 */
	public DataAndToken buildDataAndToken(Object entity) {
		DataAndToken result;
		try {
			result = securitySign.buildDataAndToken(entity);
			return result;
		} catch (SecurityException e) {
			LOGGER.error("build data and token failed! 构建data和token失败!", e);
		}
		return null;
	}
	
	/**
	 * 根据数据对象实体构建data
	 * @param entity Object
	 * @return
	 */
	public String buildData(Object entity) {
		DataAndToken dataAndToken = buildDataAndToken(entity);
		return dataAndToken.getData();
	}
	
	/**
	 * 根据数据对象实体构建token
	 * @param entity Object
	 * @return
	 */
	public String buildToken(Object entity) {
		DataAndToken dataAndToken = buildDataAndToken(entity);
		return dataAndToken.getToken();
	}
	
	/**
	 * 根据数据对象构建URI
	 * @param entity Object
	 * @return
	 */
	public URI buildURI(Object entity) {
		String uri;
		URI uriObj = null;
		try {
			DataAndToken dataAndToken = securitySign.buildDataAndToken(entity);
			if (null == dataAndToken) {
				return uriObj;
			}
			uri = SecurityConstants.DATA + EQUALS + dataAndToken.getData() + AND_STR
					+ SecurityConstants.TOKEN + EQUALS + dataAndToken.getToken();
			uriObj = URI.create(uri);
		} catch (SecurityException e) {
			LOGGER.error("build URI failed! 构建URI失败!", e);
		}
		return uriObj;
	}
	
	public String buildData(String dataSource) {
		String result;
		try {
			result = securitySign.sign(dataSource);
			return result;
		} catch (SecurityException e) {
			LOGGER.error("build data and token failed! 构建data和token失败!", e);
		}
		return null;
	}
	
	public String buildToken(String dataSource) {
		String result;
		try {
			result = securitySign.sign(dataSource);
			return result;
		} catch (SecurityException e) {
			LOGGER.error("build data and token failed! 构建data和token失败!", e);
		}
		return null;
	}

}
