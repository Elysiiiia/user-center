package com.example.demo.service;

import com.example.demo.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


/**
* @author 11368
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-25 13:55:07
*/
public interface UserService extends IService<User> {
    /**
     * 用户登录态键
     */
    //String USER_LOGIN_STATE="userLoginState";
    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param planetCode 编号
     * @return 新用户ID
     */
    long userRegister(String userAccount, String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount //用户账户
     * @param userPassword //用户密码
     * @param request
     * @return //脱敏信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户登出
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searcherUsersByTags(List<String> tagNameList);

    /**
     * 更新用户
     * @param user
     * @return
     */

    int userUpdate(User user, User loginUser);

    /**
     * 获取登录用户
     */

    User getLoginUser(HttpServletRequest request);

    /**
     * 鉴定是否为管理员
     * @return  //是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 鉴定是否为管理员
     * @return  //是否为管理员
     */
    boolean isAdmin(User user);


    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
