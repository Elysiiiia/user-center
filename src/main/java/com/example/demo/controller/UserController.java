package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtils;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.domain.User;
import com.example.demo.model.domain.request.UserLoginRequest;
import com.example.demo.model.domain.request.UserRegisterRequest;
import com.example.demo.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.example.demo.contant.UserConstant.ADMIN_ROLE;
import static com.example.demo.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author eylsia
 */
@Data
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest==null)
        {
           // return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount=userRegisterRequest.getUserAccount();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkPassword=userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode))
        {
            return null;
        }
        long result=userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(result);
    }
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount=userLoginRequest.getUserAccount();
        String userPassword=userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user=userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if(request==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result=userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj= request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User)userObj;
        if(currentUser==null)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN   );
        }
        long userId=currentUser.getId();
        //  TODO    校验用户是否为空
        User user=userService.getById(userId);
        User safetyUser=userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username ,HttpServletRequest request) {

        if(!isAdmin(request))
    {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list= userList.stream().map(user-> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request) {
       if(!isAdmin(request))
       {
           throw new BusinessException(ErrorCode.NO_AUTH);
       }
        if(id<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b= userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private  boolean isAdmin(HttpServletRequest request){
        //鉴权,仅管理员可查询
        Object userObj= request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if(user==null || user.getRole()!=ADMIN_ROLE ) {
            return false;
        }
        return true;
    }

}
