package com.example.redistest.service;

import java.util.List;

public interface RedisService {
    void set(String key, String value);

    String get(String get);

    boolean expire(String key, long expire);

    void remove(String key);

    Long increment(String key, long delta);

    boolean sismember(String key, Object value);

    Long decr(String key);

    Long sadd(String key, String value);

    void multi();

    List<Object> exec();
}
