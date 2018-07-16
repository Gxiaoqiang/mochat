package com.security.md;

import java.io.UnsupportedEncodingException;

import com.security.utils.SercurityToolUtils;

/**
 * MD5认证类
 *
 */
public final class MD5Verify {
	
	private static final String UTF8 = "UTF-8";

	/** 秘钥salt password*/
	private String salt;
	
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	/**
	 * 验证data和token
	 * @param data
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	public boolean verifySignature(String data, String token) throws Exception {		
		try {
			return doVerifySignatureWithBase64(data, token);
		} catch (Exception e) {
			throw new Exception("verify data and token failed! 验证data和token失败!", e);
		}
	}
	
	
	/**
	 * 根据data生成新的tokenNew与token进行验证
	 * @param data
	 * @param token
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private boolean doVerifySignatureWithBase64(String data, String token) throws UnsupportedEncodingException {
		String dataEncode = new String(SercurityToolUtils.decodeBase64(data), UTF8);
		String tokenNew = getTokenNew(dataEncode, getSalt());
		if (tokenNew.equals(token)) {
			return true;
		} else {
			return false;
		}		
	}
	
	private String getTokenNew(String data ,String salt) throws UnsupportedEncodingException {
		String mdToken = SercurityToolUtils.getMD5Str(data + salt);
		return new String(SercurityToolUtils.encodeBase64(mdToken));
	}
}
