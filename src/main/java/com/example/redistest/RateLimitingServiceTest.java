package com.example.redistest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author 当我遇上你
 * @公众号 当我遇上你
 * @since 2020-05-21
 */
//@SpringBootTest
@RestController
class RateLimitingServiceTest {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript<Long> redisScript;

    @Test
    public void luaTest() {
        List<String> keys = Arrays.asList("aaa");
        // 10秒内小于或等于3次时返回1，否则返回0
        for (int i = 0; i < 4; i++) {
            Object execute = redisTemplate.execute(redisScript, keys, 10, 3);
            System.out.println(execute);
        }
    }
}
