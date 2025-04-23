package com.example.demo.model.domain.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 */

@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

    public String getUserAccount() {
        return userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public String getPlanetCode() { return planetCode; }


}
