package com.example.demo.service;

import com.example.demo.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("spyString","dog");
        valueOperations.set("spyInt",1);
        valueOperations.set("spyDouble",2.000);
        User user=new User();
        user.setId(1L);
        user.setUsername("spy");
        user.setPassword("12345678");
        valueOperations.set("spyUser",user);
        //查
        Object spy = valueOperations.get("spyString");
        Assertions.assertTrue("dog".equals((String) spy));
        spy = valueOperations.get("spyInt");
        Assertions.assertTrue(1 == (Integer) spy);
        spy = valueOperations.get("spyDouble");
        Assertions.assertTrue(2.0 == (Double) spy);
        System.out.println(valueOperations.get("spyUser"));
        //删
        //改
    }
}
