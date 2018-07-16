package com.security.md;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.security.constants.SecurityConstants;
import com.security.parser.DataParser;
import com.security.parser.StringDataParser;
import com.security.utils.SercurityToolUtils;
import com.security.verify.SecurityVerify;

/**
 * MD5认证策略 
 * @author 80374523
 *
 */
public class MD5SecurityVerifyStrategy implements SecurityVerify {
	
	/** md5认证*/
	private MD5Verify md5Verify;
	
	/** 数据解析**/
	private DataParser dataParser;
	
	/** 密码**/
	private String secret;
	
	/** 有效期  */
	private int validDay = -1;
	
	private void init() {
		dataParser = new StringDataParser();
		md5Verify = new MD5Verify();
	}
	
	public MD5SecurityVerifyStrategy() {
		init();
	}

	public MD5SecurityVerifyStrategy(String secret, int validDay) {
		init();
		
		this.secret = secret;
		md5Verify.setSalt(secret);
		this.validDay = validDay;
	}
	@Override
	public String getVerifyStrategyName() {
		return "MD5认证";
	}
	
	public int getValidDay() {
		return validDay;
	}

	public void setValidDay(int validDay) {
		this.validDay = validDay;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
		md5Verify.setSalt(secret);
	}
	
	public DataParser getDataParser() {
		return dataParser;
	}

	@Override
	public void setDataParser(DataParser dataParser) {
		this.dataParser = dataParser;
	}

	@Override
	public boolean verify(String data, String token) throws SecurityException {
		if ((data == null || data.trim().isEmpty()) || (token == null || token.trim().isEmpty())) {
			return false;
		}
		try {
			data = SercurityToolUtils.formatBase64(data);
			token = SercurityToolUtils.formatBase64(token);
			if (validDay(data)) {
				return md5Verify.verifySignature(data, token);
			}
		} catch (Exception e) {
			throw new SecurityException("md5 verify failure! 消息摘要MD5认证失败", e);			
		}
		return false;
	}
	
	/**
	 * 验证日期
	 * @param data
	 * @return
	 * @throws SecurityException 
	 */
	private boolean validDay(String data) throws SecurityException {
		if (validDay <= 0) {
			return true;
		}
		String dataDecode = data;
		try {
			dataDecode = new String(SercurityToolUtils.decodeBase64(data), SecurityConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException("UnsupportedEncoding:", e);
		}
		long dataTime = dataParser.parseTime(dataDecode);
		long daySubResult = new Date().getTime() - dataTime;
		if (daySubResult <= validDay) {
			return true;
		}
		return false;
		
	}

	@Override
	public Object getDataEntity(String data) throws SecurityException {
		String dataDecode;
		try {
			dataDecode = new String(SercurityToolUtils.decodeBase64(data), SecurityConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException("UnsupportedEncoding:", e);
		}
		return dataParser.parseDataEntity(dataDecode);
	}

}
