package com.example.redistest.service.impl;

import com.example.redistest.service.RedisService;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set (String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get (String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean expire(String key, long exprie) {
        return stringRedisTemplate.expire(key, exprie, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public boolean sismember(String key, Object value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long decr(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    @Override
    public Long sadd(String key, String value) {
        return stringRedisTemplate.opsForSet().add(key, value);
    }

    @Override
    public void multi() {
        stringRedisTemplate.multi();
    }

    @Override
    public List<Object> exec() {
        return stringRedisTemplate.exec();
    }
}
