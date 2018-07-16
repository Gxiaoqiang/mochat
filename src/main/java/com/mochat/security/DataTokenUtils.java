/**
 * 
 */
package com.mochat.security;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mochat.model.UserInfo;
import com.security.utils.SercurityToolUtils;

/**
 * @author 80375043
 *
 */
public class DataTokenUtils {
	
	private static final String DATA_SPLIT = "|";
	
	private static final String DATA_EXPRESS = "=";
	
	private static final String NULL = "null";
	private static final String USER_ID = "userId";
	private static final String USER_NAME = "userName";
	private static final String TIME = "time";
	private static final String LOGIN_TIME = "loginTime";
	private static final String IP = "ip";
	private static final String SAP_ID = "sapId";
	private static final String ORG_ID = "orgId";
	private static final String EMAIL = "eamil";
	private static final String NET = "net";
	
	public static String md5TokenBuilder(String data,String salt){
		if(data == null)
			return null;
		
		String data1 = SercurityToolUtils.formatBase64(data);
		String token = null;
		try {
			String data2 = new String(SercurityToolUtils.decodeBase64(data1), "UTF-8");
			String mdToken = SercurityToolUtils.getMD5Str(data2 + salt);
			token = new String(SercurityToolUtils.encodeBase64(mdToken));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return token;
	}
	
	public static void setValueToCookie(HttpServletResponse httpResponse,
			HttpServletRequest httpRequest, String key, String value, int expiry) {
		Cookie cookie = new Cookie(key, value);
		cookie.setPath((new StringBuilder())
				.append(httpRequest.getContextPath()).append("/").toString());
		cookie.setMaxAge(expiry);
		httpResponse.addCookie(cookie);
	}
	
	/**
	 * @param userInfo
	 * @return
	 */
	public static String buildCookieData(UserInfo user) {
		Date nowDate=new Date();    
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

		StringBuilder sb = new StringBuilder()
			.append(USER_ID).append(DATA_EXPRESS).append(user.getUserId()).append(DATA_SPLIT)
			.append(TIME).append(DATA_EXPRESS).append(df.format(nowDate)).append(DATA_SPLIT)
			.append(LOGIN_TIME).append(DATA_EXPRESS).append(df.format(nowDate)).append(DATA_SPLIT)
			.append(USER_NAME).append(DATA_EXPRESS).append(user.getUserName()).append(DATA_SPLIT);
		return sb.toString();
	}
}