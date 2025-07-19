package com.example.demo.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);
    @Autowired
    private RedissonClient redissonClient;

    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 40,41,42,43 22 * * ?   ")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("user:recommend:lock");
        try{
            if(lock.tryLock(0,3000L, TimeUnit.MILLISECONDS)) {
                System.out.println("getlock"+Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        System.out.println("redis set key error");
                    }
                }
            }
    }catch (Exception ignored){

        }finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
                System.out.println("unlock"+Thread.currentThread().getId());
            }
        }
    }
//    @Resource
//    private UserService userService;
//
//    @Resource
//    private RedisTemplate<String,Object> redisTemplate;
//
//    private final List<Long> mainUserIds=Arrays.asList(3L);
//
//    @Scheduled(cron = "0 3 0 * * ? * ")
//    public void doCacheRecommendUser(){
//        for(Long userId:mainUserIds){
//            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//            Page<User> users=userService.page(new Page<>(1,10),queryWrapper);
//            ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
//            String redisKey=String.format("推荐用户:user:%s",userId);
//            try {
//                valueOperations.set(redisKey, users, 30000, TimeUnit.MILLISECONDS);
//            }catch(Exception e)
//            {
//                e.printStackTrace();
//                System.out.println("定时推荐错误");
//            }
//        }
//    }
}