package xyz.zzj.springbootxztxbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.mapper.TeamMapper;
import xyz.zzj.springbootxztxbackend.model.domain.Team;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.UserTeam;
import xyz.zzj.springbootxztxbackend.model.domain.dto.TeamQueryDTO;
import xyz.zzj.springbootxztxbackend.model.domain.enumTeam.TeamStatesEnum;
import xyz.zzj.springbootxztxbackend.service.TeamService;
import xyz.zzj.springbootxztxbackend.service.UserTeamService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author zengz
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-02-04 22:28:42
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService{


    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     * @param team
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //请求参数是否为空
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        //校验是否登录
        if (loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        final Long userId = Optional.ofNullable(loginUser.getId()).orElse(0L);
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        - 队伍 >= 1 且 <=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数过多或者过少");
        }
//        - 队伍标题 <=20
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标题不满足要求");
        }
//        - 描述 <=512
        String teamDescription = team.getTeamDescription();
        if (StringUtils.isNotBlank(teamDescription)&& teamDescription.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"这么能写，帮我写毕业论文");
        }
//        - 是否公开，不填默认公开
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatesEnum teamStatesEnum = TeamStatesEnum.getEnumTeam(status);
        if (teamStatesEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        - 加密状态，密码 <= 32
        String teamPassword = team.getPassword();
        if (TeamStatesEnum.SECRET.equals(teamStatesEnum)){
            if (StringUtils.isBlank(teamPassword) || teamPassword.length() > 32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码格式不对");
            }
        }
//        - 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"过期时间不对哦。");
        }
//        - 用户最多创建5个队伍
        //todo 可能有bug，可能会有100+创建
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        long count = this.count(queryWrapper);
        if (count >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建5个队伍");
        }
        team.setId(null);
//        - 插入队伍信息到队伍表
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
//        - 插入用户 => 队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        boolean save = userTeamService.save(userTeam);
        if (!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }


    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    public boolean deleteTeam(long id) {
        if (id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int deleteById = teamMapper.deleteById(id);
        if (deleteById <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"删除失败");
        }
        return true;
    }

    /**
     * 修改
     * @param team
     * @return
     */
    @Override
    public boolean updateTeam(Team team) {
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (team.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int updateById = teamMapper.updateById(team);
        if (updateById <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新失败");
        }
        return true;
    }

    /**
     * 查询全部
     * @param teamQueryDTO
     * @return
     */
    @Override
    public List<Team> listTeam(TeamQueryDTO teamQueryDTO) {
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryDTO,team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> list = teamMapper.selectList(queryWrapper);
        if (list == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询失败");
        }
        return list;
    }

    /**
     * 分页查询
     * @param teamQueryDTO
     * @return
     */
    @Override
    public Page<Team> listTeamPage(TeamQueryDTO teamQueryDTO) {
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryDTO,team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Page<Team> page = new Page<>(teamQueryDTO.getPageNum(),teamQueryDTO.getPageSize());
        Page<Team> teamPage = teamMapper.selectPage(page, queryWrapper);
        if (teamPage == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return teamPage;
    }
}




