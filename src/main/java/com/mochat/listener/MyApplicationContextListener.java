package com.mochat.listener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.mochat.constant.RedisConstants;
import com.mochat.rabbitmq.RabbitRandomComsumer;
import com.mochat.rabbitmq.RabbitRandomPublish;
import com.mochat.redis.CustomRedisClusters;
import com.mochat.redis.RedisMsgListener;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

public class MyApplicationContextListener implements ApplicationContextAware, DisposableBean{

	private static ApplicationContext applicationContext;

	private static JedisPubSub jedisPubSub;
	
	private static RabbitRandomPublish rabbitRandomPublish;
	
	private static RabbitRandomComsumer rabbitRandomComsumer;
	
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		System.out.println("******************************************");
		if(context.getParent() == null) {
		  MyApplicationContextListener.applicationContext = context;
		  rabbitRandomComsumer = (RabbitRandomComsumer)context.getBean(RabbitRandomComsumer.class);
		  rabbitRandomPublish = (RabbitRandomPublish)context.getBean(RabbitRandomPublish.class);
		  rabbitRandomComsumer.init();
		  rabbitRandomPublish.init();
		  new Thread(
				  new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						rabbitRandomComsumer.bind();
					}
				}).start();
		}
	}
	
	/**
	 * 启动时订阅队列
	 */
   private static final void subRedis() {
	   CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
	   JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
	    jedisPubSub = new RedisMsgListener();
		jedisCluster.subscribe(jedisPubSub, RedisConstants.SUB_PUB_CHANNEL);// 订阅队列
   }
	
   public static  Object getBean(String name) {
	  return  applicationContext.getBean(name);
   }
   
   public static Object getBean(Class<?> clazz) {
	   return applicationContext.getBean(clazz);
   }
	public static ApplicationContext getApplication() {
		return applicationContext;
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		   CustomRedisClusters customRedisClusters = (CustomRedisClusters) MyApplicationContextListener.getBean("customRedisClusters");
		   JedisCluster jedisCluster = customRedisClusters.getJedisCluster();
		   //清理节点，取消监听，关闭连接池
		   jedisCluster.del(RedisConstants.WAIT_SET);
		   jedisCluster.del(RedisConstants.ON_LINE_SET);
		   jedisCluster.del(RedisConstants.CHAT_ING_HASH);
		   jedisCluster.close();
		   rabbitRandomComsumer.close();
		   rabbitRandomPublish.close();
		MyApplicationContextListener.applicationContext = null;
		System.out.println("9999999999999999999999999999999999999999");
		jedisPubSub.unsubscribe();
	}

}
