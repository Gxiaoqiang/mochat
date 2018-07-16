package com.mochat.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mochat.cache.ThreadLocalCache;
import com.mochat.model.ResponseEntity;
import com.mochat.model.UserInfo;
import com.mochat.service.impl.LoginService;
import com.mochat.service.impl.VerificationCodeService;
import com.rabbitmq.client.impl.Method;
@Controller
@RequestMapping(value = {"/login"})
public class LoginController extends CommonHandler{

	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private VerificationCodeService verificationCodeService;
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/register")
	public ResponseEntity<Boolean> register(@RequestBody Map<String, String> paramMap)throws Exception{
		
		return null;
	}
	
	
	@ResponseBody
	@RequestMapping("/loginOut")
	public ResponseEntity<Boolean> loginOut(HttpServletRequest request ,HttpServletResponse response )throws Exception{
		
		Boolean flag =  loginService.loginOut(request, response);
		
		return successHandle(true, flag, null);
	}
	
	/**
	 * ��¼
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value= {"/loginIn"})
	public ResponseEntity<UserInfo> login(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String userMail = request.getParameter("email");
		String passWord = request.getParameter("passWord");
		UserInfo userInfo = new UserInfo();
		
		if(StringUtils.isEmpty(userMail)||StringUtils.isEmpty(passWord)) {
			return successHandle(false, null, "邮箱或者密码不能为空！");
		}
		userInfo.setEmail(userMail);
		userInfo.setUserPassWord(passWord);
		userInfo = loginService.getUserInfo(userInfo, request, response);
		
		return successHandle(true, userInfo, null);
	}
	
	@ResponseBody
	@RequestMapping(value= {"/getUserInfo"})
	public ResponseEntity<UserInfo> getUserInfo(){
		UserInfo userInfo = ThreadLocalCache.get();
		return successHandle(true, userInfo, null);
	}
	
	@RequestMapping(value= {"/getVerificationImage"},method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getVerificationImage() throws ServletException, IOException{
		Map<String, Object> map = verificationCodeService.encodeBase64ImgCode();
		return successHandle(true, map, null);
	}
}
