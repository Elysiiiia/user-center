package com.example.demo.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户
 * &#064;TableName  user
 */
@TableName(value = "user")
@Data
public class User {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账号
     */
    private String useraccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户密码
     */
    private String userpassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-正常
     */
    private Integer userstatus;

    /**
     * 创建时间
     */
    private Integer role;
    /**
     * 用户权限
     * 1普通用户
     * 2管理员
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 编号
     */
    private String planetCode;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;



    // 无参构造函数
    public User() {
    }

    // 为所有字段添加 getter 和 setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlanetCode() {return planetCode;}

    public void setPlanetCode(String planetCode) {this.planetCode=planetCode;}


    public String getUseraccount() {
        return useraccount;
    }

    public void setUseraccount(String useraccount) {
        this.useraccount = useraccount;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(Integer userstatus) {
        this.userstatus = userstatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    // 原有的自定义方法保留
    public String getAccount() {
        return useraccount;
    }

    public void setAccount(String useraccount) {
        this.useraccount = useraccount;
    }

    public String getPassword() {
        return userpassword;
    }

    public void setPassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public Integer getStatus() {
        return userstatus;
    }

    public void setStatus(Integer userstatus) {
        this.userstatus = userstatus;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }



}