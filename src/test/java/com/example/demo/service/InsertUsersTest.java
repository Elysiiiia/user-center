package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch= new StopWatch();
        stopWatch.start();
        int batchSize=10000;
        int j=0;
        List<CompletableFuture<Void>> futureList=new ArrayList<>();
        for(int i=0;i<20;i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假名称");
                user.setPassword("be6b2ef42a8e79d6b2574bba7e6db474");
                user.setUseraccount("fakeAccount");
                user.setAvatarUrl("https://ts1.tc.mm.bing.net/th/id/OIP-C.kS5lFHkxlprmZsH1qP3esQHaFg?w=187&h=185&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2");
                user.setGender(0);
                user.setPhone("12345");
                user.setEmail("12345");
                user.setIsDelete(0);
                user.setAccount("假数据");
                user.setStatus(0);
                user.setRole(0);
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("thread:" + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
