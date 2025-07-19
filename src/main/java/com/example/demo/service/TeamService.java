package com.example.demo.service;

import com.example.demo.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.domain.TeamUserVO.TeamUserVO;
import com.example.demo.model.domain.User;
import com.example.demo.model.domain.dto.TeamQuery;
import com.example.demo.model.domain.request.JoinTeamRequest;
import com.example.demo.model.domain.request.TeamQuitRequest;
import com.example.demo.model.domain.request.TeamUpdateRequest;

import java.util.List;

/**
* @author 11368
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-05-27 23:11:37
*/
public interface TeamService extends IService<Team> {

    /**
     * 新增队伍
     * @return 新增的队伍id
     */
    Long addTeam(Team team, User user);

    /**
     *查找队伍
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,Boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     * @param joinTeamRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除队伍
     * @param id
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
