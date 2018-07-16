package com.mochat.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.mochat.cache.ThreadLocalCache;
import com.mochat.cache.ThreadLocalDataTokenMap;
import com.mochat.cache.VolitleValue;
import com.mochat.model.UserInfo;
import com.mochat.security.DataTokenUtils;
import com.mochat.security.MD5Utils;
import com.util.SystemLog;

public class MoChatFilter implements Filter{
	
	
	public static final String MODULE_ID = "";
	public static final String DATA = "data";//存放用户信息
	public static final String TOKEN = "token";//存放校验信息，判断是否过期等功能
	public static final String COOKIE_DATA = (new StringBuilder())
			.append(MODULE_ID).append("data").toString();
	public static final String COOKIE_TOKEN = (new StringBuilder())
			.append(MODULE_ID).append("token").toString();
	public static final String COOKIE_USER_ID = (new StringBuilder())
			.append(MODULE_ID).append("userId").toString();
	public static final String LOGIN_URL = "LOGIN_URL";
	public static final String EXCEPTION_URI = "EXCEPTION_URI";
	protected String errurl;
	protected List<String> excludeUriList;
	public static final String loginHanlderUrl = "login/loginIn";
	public static final String loginRegisterUrl = "login/register";
	public MoChatFilter() {
		errurl = "";
		excludeUriList = null;
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String excludeUrl = filterConfig.getInitParameter("EXCEPTION_URI");
		if (excludeUrl != null) {
			String uris[] = excludeUrl.replace("\uFF0C", ",").replace(";", ",")
					.split(",");
			if (uris != null) {
				excludeUriList = new ArrayList<String>();
				String arr$[] = uris;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++) {
					String u = arr$[i$];
					excludeUriList.add(u);
				}
			}
		}
		errurl = filterConfig.getInitParameter("ERR_URL");
		if (errurl != null)
			if (excludeUriList == null) {
				excludeUriList = new ArrayList<String>();
				excludeUriList.add(errurl);
			} else if (!excludeUriList.contains(errurl))
				excludeUriList.add(errurl);
		
		
	}

	protected Map<String, String> getQueryParameters(String queryString) {
		Map<String, String> retMap = new HashMap<String, String>();
		if (queryString != null && !"".equals(queryString)) {
			String params[] = queryString.split("&");
			String arr$[] = params;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				String s = arr$[i$];
				String arr[] = s.split("=");
				if (arr.length == 1) {
					retMap.put(arr[0], "");
					continue;
				}
				if (arr.length == 2)
					retMap.put(arr[0], arr[1]);
			}

		}
		return retMap;
	}
	private boolean doLogin(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		Map<String, String> requestParams = getQueryParameters(httpRequest.getQueryString());
		String data = null;
		String token = null;
		try {
			data = requestParams.containsKey("data")?
								URLDecoder.decode(requestParams.get("data"), "UTF-8"):null;
			token = requestParams.containsKey("token")?
								URLDecoder.decode(requestParams.get("token"), "UTF-8"):null;								
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		boolean verifyFlag = false;
		if (StringUtils.isEmpty(data) || StringUtils.isEmpty(token)) {
			return false;
		}

		verifyFlag = MD5Utils.getVerifyInstance().verifyDataToken(data, token);
		
		if (!(verifyFlag)) {
			SystemLog
					.out("The QueryString Data and Token isValidSignature fail... ");
			return false;
		}
		
		UserInfo user = getUserBySecurityFactory(data);
		if (null == user) {
			return false;
		} else {
			String userId = user.getUserId();
			DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_DATA, data, -1);
			DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_TOKEN, token, -1);
			DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_USER_ID, userId,
					-1);
			setThreadLocalMap(httpRequest, data, token, user);
			return true;
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String url = httpRequest.getRequestURI();
		if(!url.contains(loginHanlderUrl)&&!url.contains(loginRegisterUrl)){
			httpResponse
					.setHeader("P3P",
							"CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
			if (!isLogin(httpRequest)) {
				if (!doLogin(httpRequest, httpResponse)
						&& !isExcludeUri(httpRequest)) {
					doDispatcherUri(httpRequest, httpResponse, errurl);
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	protected void doDispatcherUri(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, String errUri) throws IOException {
		String defaultEncoding = httpRequest.getCharacterEncoding();
		if ("".equals(defaultEncoding) || null == defaultEncoding)
			defaultEncoding = "UTF-8";
		httpResponse.setStatus(200);
		httpResponse.setContentType((new StringBuilder())
				.append("text/html;charset=").append(defaultEncoding)
				.toString());
		httpResponse.setCharacterEncoding(defaultEncoding);
		String redirectUrl = errUri;
		/*if (errUri.startsWith("/"))
			redirectUrl = errUri.substring(1);*/
		
		httpResponse.getWriter().println(
				(new StringBuilder()).append("<script>window.location ='"+httpRequest.getContextPath()+"/")
						.append(redirectUrl).append("';</script>").toString());
		httpResponse.getWriter().flush();
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	protected boolean isExcludeUri(HttpServletRequest httpRequest) {
		String uri;
		if (this.excludeUriList != null) {
			uri = httpRequest.getRequestURI();
			for (String ex : this.excludeUriList) {
				if (uri.endsWith(ex)) {
					return true;
				}
			}
		}
		return false;
	}
	protected void clearLogin(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		if (isLogin(httpRequest)) {
			DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_DATA, null, 0);
			DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_TOKEN, null, 0);
			DataTokenUtils.setValueToCookie(httpResponse, httpRequest, COOKIE_USER_ID, null, 0);
		}
	}
	
	protected boolean isLogin(HttpServletRequest httpRequest) {

		String cookieData = getValueFromCookie(httpRequest, COOKIE_DATA);
		String cookieToken = getValueFromCookie(httpRequest, COOKIE_TOKEN);
		String cookieUserId = getValueFromCookie(httpRequest, COOKIE_USER_ID);


		if (StringUtils.isEmpty(cookieData)
					|| StringUtils.isEmpty(cookieToken)) {
				return false;
			}
			
		if(!MD5Utils.getVerifyInstance().verifyDataToken(cookieData,cookieToken)){
			return false;
		}
		UserInfo cookieUser = getUserBySecurityFactory(cookieData);
		if (null == cookieUser) {
			return false;
		} else {
			setThreadLocalMap(httpRequest, cookieData, cookieToken,
						cookieUser);
			return true;
		}
	}
	
	
	private void setThreadLocalMap(HttpServletRequest httpRequest, String data,
			String token, UserInfo cookieUser) {
		Map<String, Object> dataTokenMap = new HashMap<String, Object>();
		dataTokenMap.put("data", data);
		dataTokenMap.put("token", token);
		ThreadLocalDataTokenMap.set(dataTokenMap);
		ThreadLocalCache.set(cookieUser);
	}
	private UserInfo getUserBySecurityFactory(String data) {
		Object dataObj = MD5Utils.getVerifyInstance().getDataEntity(data);
		if(dataObj instanceof UserInfo){
			return (UserInfo)dataObj;
		}
		return null;
	}
	protected String getValueFromCookie(HttpServletRequest request, String key) {
		String cookie = null;
		Cookie cookies[] = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			Cookie arr$[] = cookies;
			int len$ = arr$.length;
			int i$ = 0;
			do {
				if (i$ >= len$)
					break;
				Cookie cc = arr$[i$];
				if (key.equals(cc.getName())) {
					cookie = cc.getValue();
					break;
				}
				i$++;
			} while (true);
		}
		return cookie;
	}

	protected String getUserId(HttpServletRequest httpRequest) {
		String userId = getValueFromCookie(httpRequest, COOKIE_USER_ID);
		return null != userId ? userId : "";
	}
	
}
