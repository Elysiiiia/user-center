package com.example.demo.model.domain.request;

import java.io.Serializable;
import java.util.Date;

public class JoinTeamRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer status;

    private String passWord;

    private Long teamId;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
