package com.mochat.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.mochat.constant.RedisConstants;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class CustomRedisClusters {
    private int maxTotal = 100;
    private int maxIdle = 5;
    private int maxWaitMillis = 1000;
    private int connectionTimeout = 5000;
    private int soTimeout = 5000;
    private int maxAttempts = 5;
    private final Map<String, JedisCluster> redisClusterMap = new HashMap<String,JedisCluster>();

    private final JedisCluster jedisCluster;
    
    public CustomRedisClusters(RedisConstants constants) {
        	jedisCluster = createCluster(constants.getRedisNodes(), constants.getPassword());
            redisClusterMap.put("redisCluster", jedisCluster);
    }
    
	private JedisCluster createCluster(String nodes, String password) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        String[] node = nodes.split(",");
        Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
        for (int i = 0; i < node.length; i++) {
            String[] hostPort = node[i].split(":");
            hostAndPorts.add(new HostAndPort(hostPort[0], Integer.valueOf(hostPort[1])));
        }
        if ("null".equals(password)) {
            return new JedisCluster(hostAndPorts, connectionTimeout, soTimeout, maxAttempts, poolConfig);
        } else {
            return new JedisCluster(hostAndPorts, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
        }
    }
	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}
    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Map<String, JedisCluster> getRedisClusterMap() {
        return redisClusterMap;
    }
}
