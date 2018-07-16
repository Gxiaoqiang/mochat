package com.mochat.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mochat.cache.ThreadLocalCache;
import com.mochat.cache.VolitleValue;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.mapper.LoginMapper;
import com.mochat.model.UserInfo;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.security.DataTokenUtils;
import com.mochat.security.MD5Utils;
import com.security.utils.SercurityToolUtils;

import redis.clients.jedis.JedisCluster;

@Service
public class LoginService {

	public static final String DATA = "data";
	public static final String TOKEN = "token";
	public static final String MOUDLEID = "Mochat";
	public static final String COOKIE_DATA = MOUDLEID + DATA;
	public static final String COOKIE_TOKEN = MOUDLEID + TOKEN;
	
	
	@Autowired
	private LoginMapper loginInterface;
	
	@Autowired
	private CustomRedisClusters customRedisCluster;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Boolean loginOut(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		DataTokenUtils.setValueToCookie(response, request, "data",null , -1);
		DataTokenUtils.setValueToCookie(response, request, "token", null, -1);
		
		DataTokenUtils.setValueToCookie(response, request, COOKIE_DATA,null , -1);
		DataTokenUtils.setValueToCookie(response, request, COOKIE_TOKEN, null, -1);
		
		return true;
	}
	
	public UserInfo getUserInfo(UserInfo userInfo,HttpServletRequest request ,HttpServletResponse response) throws Exception {
		
		String path = Constants.PROJECT_PATH.get();
		if(StringUtils.isEmpty(path)) {
			path = request.getSession().getServletContext().getRealPath("/");
			Constants.PROJECT_PATH.set(path);
		}
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		String passwdMd5 = DataTokenUtils.md5TokenBuilder(userInfo.getUserPassWord(),MD5Utils.secret);

		
		String userInfoStr = jedisCluster.get(userInfo.getEmail()+passwdMd5+RedisConstants.USER_INFO_SUFFIX);
		if(userInfoStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(userInfoStr);
			userInfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);
			userInfo.setUserPassWord(null);
			userInfo.setLoginTime(String.valueOf(System.currentTimeMillis()));
			setValueToCookie(response, request, userInfo,  0);
			
			return userInfo;
		}
		
		userInfo.setUserPassWord(passwdMd5);
		
		UserInfo userInfo2 = loginInterface.getUserInfo(userInfo);
		
		if(userInfo2 == null) {
			return null;
		}
		setValueToCookie(response, request, userInfo2,  0);
		userInfo2.setLoginTime(String.valueOf(System.currentTimeMillis()));
		String userInfoStrTemp = JSONObject.toJSONString(userInfo2);
		jedisCluster.set(userInfo2.getEmail()+passwdMd5+RedisConstants.USER_INFO_SUFFIX, userInfoStrTemp);

		jedisCluster.set(userInfo2.getUserId()+RedisConstants.USER_INFO_SUFFIX, userInfoStrTemp);
		return userInfo2;
	}
	
	private void setValueToCookie(HttpServletResponse httpResponse,
			HttpServletRequest httpRequest,UserInfo userInfo,  int expiry) throws Exception{
		String data = DataTokenUtils.buildCookieData(userInfo);
		data = new String(SercurityToolUtils.encodeBase64(data), "UTF-8");
		String token = DataTokenUtils.md5TokenBuilder(data, MD5Utils.secret);
		
		//
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, DATA,data , -1);
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, TOKEN, token, -1);
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, "userId", token, -1);
/*		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_DATA,data , -1);
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_TOKEN, token, -1);*/
		
		HttpSession session = httpRequest.getSession(true);
		session.setAttribute(RedisConstants.USER_INFO_SUFFIX, userInfo);
	}
	
}
