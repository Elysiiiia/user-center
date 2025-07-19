package com.example.demo.once;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
//  @Scheduled(initialDelay = 5000,fixedDelay = Long.MAX_VALUE)
@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 注入数据
     */
   // @Scheduled(initialDelay = 5000,fixedDelay = Long.MAX_VALUE)
    public void doInsertUsers(){
        StopWatch stopWatch= new StopWatch();
        stopWatch.start();
        final int INSERT_NUM=1000;
        for(int i=1;i<=INSERT_NUM;i++){
            User user=new User();
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
            userMapper.insert(user);
        }
        stopWatch.stop();
        stopWatch.getTotalTimeMillis();
    }
}