package com.mochat.service;

import redis.clients.jedis.JedisPubSub;

public interface PubSubInterface {
	
	public void createPub(String channel);
	
	public void createSub(String channel,JedisPubSub jedisPubSubLinstener);

}
