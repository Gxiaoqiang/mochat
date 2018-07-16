package com.mochat.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.mochat.constant.RedisConstants;

@Component
public class RedisConfig {

    @Autowired
    private RedisConstants constants;

    @Bean
    public CustomRedisClusters customRedisClusters() {
        return new CustomRedisClusters(constants);
    }
}
