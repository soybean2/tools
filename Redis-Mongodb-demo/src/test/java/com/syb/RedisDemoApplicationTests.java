package com.syb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
class RedisDemoApplicationTests {

    /**
     * 1.导包
     * 2.注入RedisTemplate
     * 3.操作redis
     * opsForValue() 操作字符串
     * opsForHash() 操作hash
     * opsForList() 操作list
     * opsForSet() 操作set
     * opsForZSet() 操作zset
     * opsForGeo() 操作geo
     * opsForHyperLogLog() 操作HyperLogLog
     */

    // redisTemplate.opsForValue() 操作对象，内部进行序列化
    @Autowired
    private RedisTemplate redisTemplate;

    // stringRedisTemplate.opsForValue() 操作字符串，操作与redis命令一致（常用）
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    void set() {
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set("age",41);
    }
    @Test
    void get() {
        ValueOperations ops = redisTemplate.opsForValue();
        Object age = ops.get("age");
        System.out.println(age);
    }
    @Test
    void hset() {
        HashOperations ops = redisTemplate.opsForHash();
        ops.put("info","b","bb");
    }
    @Test
    void hget() {
        HashOperations ops = redisTemplate.opsForHash();
        Object val = ops.get("info", "b");
        System.out.println(val);
    }

}
