package com.security.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;

import com.security.constants.SecurityConstants;

/**
 * BI安全认证工具类
 *
 */
public class SercurityToolUtils {
	
	private static final Logger LOGGER = Logger.getLogger(SercurityToolUtils.class);
	
	public static byte[] encodeBase64(byte[] binaryData) {
		return Base64.encodeBase64(binaryData);
	}
	
	public static byte[] encodeBase64(String data) {
		try {
			return Base64.encodeBase64(data.getBytes(SecurityConstants.UTF8));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncoding:", e);
			return null;
		}
	}
	
	public static byte[] decodeBase64(byte[] base64Data) {
		return Base64.decodeBase64(base64Data);
	}
	
	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data);
	}
	
	public static String getMD5Str(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance(SecurityConstants.MD5);
			md.update(plainText.getBytes(SecurityConstants.UTF8));
			byte b[] = md.digest();
			int i;

			StringBuffer buf = new StringBuffer();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("not such Algorithm:", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncoding:", e);
			return null;
		}
	}
	
	public static String getSha1Str(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance(SecurityConstants.SHA1);
			md.update(plainText.getBytes(SecurityConstants.UTF8));
			byte buff[] = md.digest();
			StringBuilder b = new StringBuilder();
			char ASCII[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
					'B', 'C', 'D', 'E', 'F' };
			for (int i = 0; i < buff.length; i++) {
				int item = buff[i] & 255;
				if (item < 16) {
					b.append('0').append(ASCII[item]);
				} else {
					b.append(ASCII[item / 16]).append(ASCII[item % 16]);
				}				
			}
			return b.toString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("not such Algorithm:", e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncoding:", e);
		}
		return null;
	}
	/*
	 * 格式化为base64格式--自动补齐
	 */
	public static String formatBase64(String token) {
		switch(token.length()%4){
		case 1:
			token += "=";
		case 2:
			token += "=";
		case 3:
			token += "=";
		}
		return token;
	}
	
	
}
