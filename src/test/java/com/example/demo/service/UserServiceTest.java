package com.example.demo.service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.example.demo.service.impl.UserServiceImpl;

import com.example.demo.model.domain.User;
import jakarta.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        MessageDigest md5=MessageDigest.getInstance("MD5");
        String newPassword= DigestUtils.md5DigestAsHex(("123456"+"myPassword".getBytes()).getBytes());
        System.out.println(newPassword);
    }
    @Test
    public void testAddUser() {
        User user=new User();
        user.setId(0L);
        user.setUsername("elysia");
        user.setAccount("");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setPassword("");
        user.setPhone("");
        user.setEmail("");
        user.setStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        boolean result = userService.save(user); // 调用 MyBatis-Plus 的 save 方法
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }


    @Test
    void userRegister() {
        String userAccount="kiana";
        String password="";
        String checkPassword="123456789";
        String planetCode="1";
        long result= userService.userRegister(userAccount,password,checkPassword,planetCode);
        assertEquals(-1, result);
        userAccount="ka";
        result= userService.userRegister(userAccount,password,checkPassword,planetCode);
        assertEquals(-1, result);
        userAccount="kiana";
        password="12345678";
        planetCode="1";
        result= userService.userRegister(userAccount,password,checkPassword,planetCode);
        assertEquals(-1, result);
        userAccount="ki ana";
        password="1234567890";
        checkPassword="1234567890";
        planetCode="1";
        result= userService.userRegister(userAccount,password,checkPassword,planetCode);
        assertEquals(-1, result);
        userAccount="kiana";
        password="12345689";
        checkPassword  ="12345689";
        planetCode="2";
        result= userService.userRegister(userAccount,password,checkPassword,planetCode);
        assertTrue(result>0);
    }

    @Test
    public void testSearchUsersByTags() {
        List<String> tagNameList= Arrays.asList("java");
        List<User> userList =userService.searcherUsersByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }

}