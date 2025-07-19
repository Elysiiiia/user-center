package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.model.domain.UserTeam;
import com.example.demo.service.UserTeamService;
import com.example.demo.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 11368
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2025-05-27 23:11:24
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




