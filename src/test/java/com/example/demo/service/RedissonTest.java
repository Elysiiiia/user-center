package com.example.demo.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        //list
        List<String> list=new ArrayList<>();
        list.add("spy");
        System.out.println("list:"+list.get(0));

        //数据存在redis内存中
        RList<String> rList=redissonClient.getList("test-list");
        rList.add("spy");
        System.out.println("rList:"+rList.get(0));
    }
}
