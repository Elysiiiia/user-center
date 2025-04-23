package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.demo.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 11368
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-03-25 13:55:07
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    @Resource
    private UserMapper userMapper;
    /**
     * 盐，混淆密码
     */
    private static final String SALT="yupi";
    /**
     * 用户登录态键
     */
    //public static final String USER_LOGIN_STATE="userLoginState";

    /**
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param planetCode 用户编号
     * @return
     */

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            // todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户过短");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        if(planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户编号过长");
        }
        //账户不能包含特殊字符
        String validPattern="\\pP|\\pS|\\s+";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 密码和校验密码不同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("useraccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count>0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        //编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count>0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号重复");
        }
        //2.加密
        String encryptPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        //3.插入数据
        User user=new User();
        user.setAccount(userAccount);
        user.setPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult=this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if(userAccount.length()<4){
            return null;
        }
        if(userPassword.length()<8){
            return null;
        }

        //账户不能包含特殊字符
        String validPattern="\\pP|\\pS|\\s+";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return null;
        }
        //2.加密
        final String SALT="yupi";
        String encryptPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        //查询账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("useraccount",userAccount);
        queryWrapper.eq("userpassword",encryptPassword);
        User user= userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user == null) {
            //log.info("user login failed,userAccount cannot match userPassword");
            return null;
        }
        //3.用户脱敏
        User safetyUser=getSafetyUser(user);
        //4.记录用户得登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
    @Override
    public User getSafetyUser(User originUser)
    {
        if(originUser==null)
        {
            return null;
        }
        User safetyUser=new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setAccount(originUser.getAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setRole(originUser.getRole());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        return safetyUser;
    }


    /**
     * 请求用户注销
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }



}