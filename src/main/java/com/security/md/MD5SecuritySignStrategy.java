package com.security.md;

import java.io.UnsupportedEncodingException;

import com.security.base.DataAndToken;
import com.security.builder.DataBuilder;
import com.security.builder.StringDataBuilder;
import com.security.constants.SecurityConstants;
import com.security.sign.SecuritySign;
import com.security.utils.SercurityToolUtils;
/**
 *
 * @ProjectName :report
 *
 * @Package:com.cmb.base.encode.Encodedata.java
 *
 * @ClassName:Encodedata
 *
 * @Description:MD5加密
 *
 *
 */
public class MD5SecuritySignStrategy implements SecuritySign {
	
	/** 秘钥secret  password*/
	private String secret ;
	
	/** data builder 数据构造 **/
	private DataBuilder dataBuilder;
	
	private void init () {
		dataBuilder = new StringDataBuilder();
	}
	
	public MD5SecuritySignStrategy() {
		init();
	}
	
	public MD5SecuritySignStrategy(String secret) {
		init();
		
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret; 
	}

	@Override
	public String getStrategyName() {
		return "MD5加密";
	}

	public DataBuilder getDataBuilder() {
		return dataBuilder;
	}

	@Override
	public void setDataBuilder(DataBuilder dataBuilder) {
		this.dataBuilder = dataBuilder;
	}

	@Override
	public String sign(String data) throws SecurityException {
		String UserEntityStrChange = data + secret;
		byte[] tokenbyte = SercurityToolUtils.encodeBase64(SercurityToolUtils.getMD5Str(UserEntityStrChange));
		String token = new String(tokenbyte);
		return token;
	}

	@Override
	public DataAndToken buildDataAndToken(Object entity) throws SecurityException {
		String data = dataBuilder.buildEntityData(entity);
		String encryptData;
		try {
			encryptData = new String(SercurityToolUtils.encodeBase64(data), SecurityConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException("UnsupportedEncoding:", e);
		}
		String token = sign(data);
		DataAndToken dataAndToken = new DataAndToken();
		dataAndToken.setData(encryptData);
		dataAndToken.setToken(token);
		return dataAndToken;
	}

	public DataAndToken buildDataAndToken(String data) throws SecurityException {
		String encryptData;
		try {
			encryptData = new String(SercurityToolUtils.encodeBase64(data), SecurityConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new SecurityException("UnsupportedEncoding:", e);
		}
		String token = sign(data);
		DataAndToken dataAndToken = new DataAndToken();
		dataAndToken.setData(encryptData);
		dataAndToken.setToken(token);
		return dataAndToken;
	}
	
}
