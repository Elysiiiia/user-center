package com.example.demo.service.impl;

import org.apache.commons.math3.util.Pair;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;

import com.example.demo.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.demo.contant.UserConstant.ADMIN_ROLE;
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
        safetyUser.setTags(originUser.getTags());
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

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searcherUsersByTags(List<String> tagNameList) {

    if(CollectionUtils.isEmpty(tagNameList))
    {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    for(String tagName:tagNameList){
        queryWrapper=queryWrapper.like("tags",tagName);
    }
    List<User> userList=userMapper.selectList(queryWrapper);
    return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
 //内存查询
        // 1. 先查询所有用户
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> userList = userMapper.selectList(queryWrapper);
//        Gson gson = new Gson();
//        // 2. 在内存中判断是否包含要求的标签
//        return userList.stream().filter(user -> {
//            String tagsStr = user.getTags();
//            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
//            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
//            for (String tagName : tagNameList) {
//                if (!tempTagNameSet.contains(tagName)) {
//                    return false;
//                }
//            }
//            return true;
//        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int userUpdate(User user, User loginUser) {
        long userId=user.getId();
        if(user.getId()<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(!isAdmin(loginUser)&& userId!=loginUser.getId() )
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User oldUser=userMapper.selectById(user.getId());
        if(oldUser==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //userMapper.updateById进行更新，为null的值将默认不更新
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userobj=request.getSession().getAttribute(USER_LOGIN_STATE);
        return (User)userobj;
    }

    @Override
    public  boolean isAdmin(HttpServletRequest request){
        //鉴权,仅管理员可查询
        Object userObj= request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if(user==null || user.getRole()!=ADMIN_ROLE ) {
            return false;
        }
        return true;
    }

    @Override
    public  boolean isAdmin (User user){
        //鉴权,仅管理员可查询
        if(user==null || user.getRole()!=ADMIN_ROLE ) {
            return false;
        }
        return true;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }
}