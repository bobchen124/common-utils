package com.bob.pn.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author chenbo@guworks.cc
 * @title xxx
 * @date 2018年09月22日
 * @since v1.0.0
 */
@Component
public class RedisDemo {

    @Autowired
    StringRedisTemplate redisTemplate;

    public void isLock() {
        //redisTemplate.get
    }

}
