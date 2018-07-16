/*package com.mochat.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;

import com.mochat.cache.ThreadLocalCache;
import com.mochat.constant.Constants;
import com.mochat.constant.RedisConstants;
import com.mochat.model.UserInfo;
import com.mochat.redis.CustomRedisClusters;

import redis.clients.jedis.JedisCluster;

public class MochatSessionListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent se) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
 	    JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
		UserInfo currentUserInfo = ThreadLocalCache.get();
		if(currentUserInfo == null) {
			Object object = se.getSession().getAttribute(Constants.USER_SESSION);
			if(object != null) {
				currentUserInfo = (UserInfo)object;
			}
			
		}
		if(currentUserInfo != null) {
			String userId = currentUserInfo.getUserId();
			if(!StringUtils.isEmpty(userId)) {
				jedisCluster.srem(RedisConstants.ON_LINE_SET, userId);
			}
		}
		
		
	}

}
*/