package com.mochat.security;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.mochat.model.UserInfo;
import com.security.parser.DataParser;

public class MochatDataParser implements DataParser {
	
	
	private static final String DATA_SPLIT = "\\|";
	
	private static final String DATA_EXPRESS = "=";
	
	private static final String NULL = "null";
	private static final String USER_ID = "userId";
	private static final String USER_NAME = "userName";
	private static final String USER_NICK_NAME = "userNickName";
	private static final String USER_MAIL = "userMail";
	private static final String LOGIN_TIME = "loginTime";
	private static final String IP = "ip";
	
	@Override
	public UserInfo parseDataEntity(String data) {
		
		UserInfo result = null;
		if (data != null && data.trim().length() > 0) {
			try {
				//data = new String(SercurityToolUtils.decodeBase64(data), "UTF-8");

				result = new UserInfo();
				String fields[] = data.split(DATA_SPLIT);
				for (int i = 0; fields != null && i < fields.length; i++) {
					String field = fields[i];
					int x = field.indexOf(DATA_EXPRESS);
					if (x != -1) {
						String key = field.substring(0, x);
						String val = field.substring(x + 1);
						if (NULL.equals(val)) {
							continue;
						}
						
						if (USER_ID.equals(key)) {
							result.setUserId(val);
						} else if (USER_NAME.equals(key)) {
							result.setUserName(val);
						} else if (LOGIN_TIME.equals(key)) {
							result.setLoginTime(val);
						} else if (IP.equals(key)) {
							result.setIp(val);
						}  else if (USER_MAIL.equals(key)) {
							result.setEmail(val);
						} else if (USER_NICK_NAME.equals(key)) {
							result.setNickname(val);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (result != null && result.getUserId() == null) {
			return null;
		}
		return result;
	}

	
	@Override
	public Long parseTime(String data) {
		UserInfo userInfo = null;
		if((userInfo = parseDataEntity(data)) != null){
			String timeStr = userInfo.getLoginTime();
			if (StringUtils.isEmpty(timeStr)) {
				return (long) 0;
			}
			timeStr = timeStr.replaceAll("T", " ");
			SimpleDateFormat sm = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			Date date;
			try {
				date = sm.parse(timeStr);
				return Long.valueOf(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return (long) 0;
	}

}
