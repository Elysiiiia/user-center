package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtils;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.domain.Team;
import com.example.demo.model.domain.TeamUserVO.TeamUserVO;
import com.example.demo.model.domain.TeamUserVO.UserVO;
import com.example.demo.model.domain.User;
import com.example.demo.model.domain.UserTeam;
import com.example.demo.model.domain.dto.TeamQuery;
import com.example.demo.model.domain.enums.TeamStatusEnum;
import com.example.demo.model.domain.request.JoinTeamRequest;
import com.example.demo.model.domain.request.TeamQuitRequest;
import com.example.demo.model.domain.request.TeamUpdateRequest;
import com.example.demo.service.TeamService;
import com.example.demo.mapper.TeamMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
* @author 11368
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2025-05-27 23:11:37
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(Team team, User loginUser) {
        if(team==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(loginUser==null)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        //1 队伍人数在1到20人之间
        int maxNum= Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1 || maxNum>20)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合要求");
        }

        String name=team.getName();

        //2 队伍名称在1到20之内
        if(name.length()>=20 || StringUtils.isBlank(name))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不符合要求");
        }
        Long userId=loginUser.getId();

        //3 队伍介绍小于512字节
        String description=team.getDescription();
        if(description.length()>=512 || StringUtils.isBlank(description))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍介绍不符合要求");
        }

        //4 队伍状态
        int status=Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum=TeamStatusEnum.getEnumByValue(status);
        if(statusEnum==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态错误");
        }

        //5 如果为密码队伍，密码不能超过20位
        String password=team.getPassWord();
        if(TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (password.length() >= 20 || StringUtils.isBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能大于20位");
            }
        }

        //6 超时时间>当前时间
        Date expireDate=team.getExpireTime();
        if(new Date().getTime()>expireDate.getTime())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超过过期时间");
        }

        //7. 校验用户最多创建五个队伍
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum=this.count(queryWrapper);
        if(hasTeamNum>=5)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍到达限制上限");
        }

        //8 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId=team.getId();
        if(!result || teamId==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"插入队伍信息到队伍表失败");
        }

        //9 插入用户信息到队伍关系表
        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TeamUserVO> listTeams(TeamQuery teamQuery,Boolean isAdmin) {
        QueryWrapper<Team> queryWrapper=new QueryWrapper<>();
        if(teamQuery!=null) {

            //按照队伍id搜索
            Long id = teamQuery.getId();
            if (id != null && id >= 0) {
                queryWrapper.eq("id", id);
            }

            List<Long> idList = teamQuery.getIdList();
            if(!CollectionUtils.isEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            //按照关键字搜索
            String searchText=teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText))
            {
                queryWrapper.and(qw->qw.like("name", searchText).or().like("description", searchText));
            }

            //按照名称 描述搜索
            String name=teamQuery.getName();
            if(StringUtils.isNotBlank(name))
            {
                queryWrapper.like("name", name);
            }
            String description=teamQuery.getDescription();
            if(StringUtils.isNotBlank(description))
            {
                queryWrapper.like("description", description);
            }


            //按最大人数搜索
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum >= 3) {
                queryWrapper.eq("maxNum", teamQuery.getMaxNum());
            }

            //按状态搜索
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum=TeamStatusEnum.getEnumByValue(status);
            if (status == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            //如果不是管理员则不给出私密房间
            if(!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE))
            {
                throw new  BusinessException(ErrorCode.PARAMS_ERROR,"无法查看私密队伍");
            }
            queryWrapper.eq("status", statusEnum.getValue());
            //按人物搜索
            Long UserId=teamQuery.getUserId();
            if(UserId != null && UserId >= 0)
            {
                queryWrapper.eq("userId", UserId);
            }
        }

        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        List<Team> teamList=this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList))
        {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList=new ArrayList<>();
        //将符合条件的队伍提取制作表单
        for(Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if(teamUpdateRequest==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id=teamUpdateRequest.getId();
        if(id==null || id<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam=getTeamById(id);
        if(loginUser.getId()!=oldTeam.getUserId() && !userService.isAdmin(loginUser))
        {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Team updateTeam=new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        this.updateById(updateTeam);
        return true;
    }

    @Override
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User loginUser) {
        if(joinTeamRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入队伍为空");
        }
        Long userId=loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper=new QueryWrapper<>();

        //加入队伍不能超过五个
        userTeamQueryWrapper.eq("userId", userId);
        long hasJoinTeam = userTeamService.count(userTeamQueryWrapper);

            if(hasJoinTeam>5)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最大加入五个队伍");
        }

        //队伍id不能为空
        Long teamId=joinTeamRequest.getTeamId();
        if(teamId==null || teamId<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id不能为空");
        }
        Team team=getById(teamId);
        //队伍过期不能加入
        Date ExpireTime=team.getExpireTime();
        if(ExpireTime.before(new Date()) && team.getExpireTime()!=null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }

        //私密队伍无法加入
        Integer staus=team.getStatus();
        TeamStatusEnum teamStatusEnum=TeamStatusEnum.getEnumByValue(staus);
        if(teamStatusEnum.equals(TeamStatusEnum.PRIVATE))
        {
            throw new BusinessException(ErrorCode.NO_AUTH,"禁止加入私有队伍");
        }

        //加密队伍输入密码
        String passWord=joinTeamRequest.getPassWord();
        if(teamStatusEnum.equals(TeamStatusEnum.SECRET))
        {
            if(passWord==null || passWord.equals(team.getPassWord()))
            {
                throw   new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }

        //已经加入该队伍
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long hasUserJoinTeam=userTeamService.count(userTeamQueryWrapper);
        if(hasUserJoinTeam>0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已经加入该队伍");
        }
        //满人数队伍无法加入
        long teamHasJoinNum=this.countTeamUserByTeamId(teamId);
        if(teamHasJoinNum > team.getMaxNum())
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已到上限");
        }
        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);

    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍参数为空");
        }
        Long teamId=teamQuitRequest.getTeamId();
        Team team=getTeamById(teamId);
        long userId=loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("teamId", teamId);
        long count=userTeamService.count(queryWrapper);
        if(count<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有加入队伍");
        }
        long teamHasJoinNum=this.countTeamUserByTeamId(teamId);
        if(teamHasJoinNum==1)
        {
            //若队伍只剩一人，则删除队伍和队伍用户关系表
            this.removeById(teamId);
        }else {
            //若队伍大于1人,若是队长，转移队长
            if (team.getUserId()==userId) {
                QueryWrapper<UserTeam> userTeamQueryWrapper=new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList=userTeamService.list(userTeamQueryWrapper);
                if(CollectionUtils.isEmpty(userTeamList) || userTeamList.size()<=1)
                {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍人数出错");
                }
                UserTeam nextUserTeam=userTeamList.get(1);
                long nextTeamLeader=nextUserTeam.getUserId();
                //更新当前队长
                Team updateTeam=new Team();
                updateTeam.setUserId(nextTeamLeader);
                updateTeam.setId(teamId);
                boolean result=updateById(updateTeam);
                if(!result)
                {
                    throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"移除队长失败");
                }
            }
        }
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public boolean deleteTeam(long id,User loginUser) {
        Team team=getTeamById(id);
        long teamId=team.getId();
        if(team.getUserId()!=loginUser.getId() && !userService.isAdmin(loginUser))
        {
            throw new BusinessException(ErrorCode.FORBIDDEN,"无权限");
        }
        QueryWrapper<UserTeam> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean  result=userTeamService.remove(queryWrapper);
        if(!result)
        {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍失败");
        }
        return this.removeById(teamId);
    }

    /**
     * 获取某队伍当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper=new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 获根据id获取队伍
     */
    private Team getTeamById(Long teamId){
        if(teamId==null || teamId<=0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id失效");
        }
        Team team=this.getById(teamId);
        if(team==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有对应的队伍");
        }
        return team;
    }
}




