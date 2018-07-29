package com.mochat.service.impl;

import java.util.Date;
import java.util.List;

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
import com.mochat.model.ResponseEntity;
import com.mochat.model.UserInfo;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.security.DataTokenUtils;
import com.mochat.security.MD5Utils;
import com.security.utils.SercurityToolUtils;
import com.util.UUIDGenerator;

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
	
	@Autowired
	private RoomChatService roomChatService;
	
	@Autowired
	private RandomChatService randomChatService;
	
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
		String loginFlag = UUIDGenerator.generate();
		if(userInfoStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(userInfoStr);
			userInfo = JSONObject.toJavaObject(jsonObject, UserInfo.class);
			userInfo.setUserPassWord(null);
			userInfo.setLoginTime(String.valueOf(System.currentTimeMillis()));
			
			setLoginFlag( jedisCluster, userInfo , loginFlag);
			setValueToCookie(response, request, userInfo,  0);
			ThreadLocalCache.set(userInfo);
			roomChatService.quitRoom();
			randomChatService.quitRandom();
			return userInfo;
		}
		
		userInfo.setUserPassWord(passwdMd5);
		
		List<UserInfo> userInfo2 = loginInterface.getUserInfo(userInfo);
		
		if(userInfo2 == null||userInfo2.size() == 0) {
			return null;
		}
		
		setLoginFlag( jedisCluster, userInfo , loginFlag);
		setValueToCookie(response, request, userInfo2.get(0),  0);
		ThreadLocalCache.set(userInfo);
		roomChatService.quitRoom();
		randomChatService.quitRandom();
		//userInfo2.get(0).setLoginTime(String.valueOf(System.currentTimeMillis()));
		String userInfoStrTemp = JSONObject.toJSONString(userInfo2.get(0));
		jedisCluster.set(userInfo2.get(0).getEmail()+passwdMd5+RedisConstants.USER_INFO_SUFFIX, userInfoStrTemp);

		jedisCluster.set(userInfo2.get(0).getUserId()+RedisConstants.USER_INFO_SUFFIX, userInfoStrTemp);
		return userInfo2.get(0);
	}
	
	
	private void setLoginFlag(JedisCluster jedisCluster,UserInfo userInfo ,String loginFlag) {
		userInfo.setFlag(loginFlag);
		jedisCluster.set(userInfo.getUserId()+RedisConstants.LOGIN_FLAG, loginFlag);
		jedisCluster.pexpire(userInfo.getUserId()+RedisConstants.LOGIN_FLAG, RedisConstants.EXPIRE_TIME*1000);
	}
	
	private void setValueToCookie(HttpServletResponse httpResponse,
			HttpServletRequest httpRequest,UserInfo userInfo,  int expiry) throws Exception{
		String data = DataTokenUtils.buildCookieData(userInfo);
		data = new String(SercurityToolUtils.encodeBase64(data), "UTF-8");
		String token = DataTokenUtils.md5TokenBuilder(data, MD5Utils.secret);
		
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, DATA,data , -1);
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, TOKEN, token, -1);
		DataTokenUtils.setValueToCookie(httpResponse, httpRequest, "userId", token, -1);
		HttpSession session = httpRequest.getSession(true);
		session.setAttribute(RedisConstants.USER_INFO_SUFFIX, userInfo);
	}
	
	
	public ResponseEntity<Boolean> register(UserInfo userInfo,HttpServletRequest request){
		if(StringUtils.isEmpty(userInfo.getUserName())) {
			return new ResponseEntity<Boolean>(false,null,"用户名称不能为空！");
		}
		if(StringUtils.isEmpty(userInfo.getEmail())) {
			return new ResponseEntity<Boolean>(false,null,"用户邮箱不能为空！");
		}
		String rePassWord = userInfo.getRePassWord();
		if(!StringUtils.equalsIgnoreCase(userInfo.getUserPassWord(), rePassWord)) {
			return new ResponseEntity<Boolean>(false,null,"密码和确认密码不一致！");
		}
		String srand = userInfo.getSrand();
		String vCode = userInfo.getvCode();
		if(StringUtils.isEmpty(srand)) {
			return new ResponseEntity<Boolean>(false,null,"验证码不能为空！");
		}
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		String vdcodeOld = jedisCluster.get(srand);
		if (vdcodeOld == null) {
			return new ResponseEntity<Boolean>(false, null, "验证码错误");
		}
		if (!StringUtils.equals(vCode.trim(), vdcodeOld.trim())) {
			return new ResponseEntity<Boolean>(false, null, "验证码错误");
		}
		jedisCluster.del(srand);
		UserInfo userInfo2 = new UserInfo();
		userInfo2.setEmail(userInfo.getEmail());
		List<UserInfo> userInfos = loginInterface.getUserInfo(userInfo2);
		if(userInfos.size()>0) {
			return new ResponseEntity<Boolean>(false, null, "邮箱已经被注册！");
		}
		String passwdMd5 = DataTokenUtils.md5TokenBuilder(userInfo.getUserPassWord(),MD5Utils.secret);
		userInfo.setUserPassWord(passwdMd5);
		String userId = UUIDGenerator.generate();
		userInfo.setUserId(userId);
		String userInfoStrTemp = JSONObject.toJSONString(userInfo);
		userInfo.setCreateTime(new Date());
		loginInterface.userRegister(userInfo);
		jedisCluster.set(userInfo.getEmail()+passwdMd5+RedisConstants.USER_INFO_SUFFIX, userInfoStrTemp);
		jedisCluster.set(userInfo.getUserId()+RedisConstants.USER_INFO_SUFFIX, userInfoStrTemp);
		return new ResponseEntity<Boolean>(true,null,null);
	}
	
	public Boolean checkLogin() {
		UserInfo userInfo = ThreadLocalCache.get();
		
		String loginFlag = userInfo.getFlag();
		
		String userId = userInfo.getUserId();
		
		JedisCluster jedisCluster = customRedisCluster.getJedisCluster();
		String oldLoginFlag = jedisCluster.get(userId+RedisConstants.LOGIN_FLAG);
		if(StringUtils.equals(loginFlag, oldLoginFlag)) {
			return true;
		}
		return false;
	}
}
