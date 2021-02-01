package org.diwayou.cache.impl;

import org.diwayou.cache.KvCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author gaopeng 2021/1/27
 */
public class RedisKvCache implements KvCache {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }
}
