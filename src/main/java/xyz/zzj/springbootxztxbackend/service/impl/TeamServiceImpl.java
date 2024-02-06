package xyz.zzj.springbootxztxbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
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
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamJoinRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamQuitRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamUpdateRequest;
import xyz.zzj.springbootxztxbackend.model.domain.vo.TeamUserVO;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.service.TeamService;
import xyz.zzj.springbootxztxbackend.service.UserService;
import xyz.zzj.springbootxztxbackend.service.UserTeamService;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private UserService userService;

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
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id,User loginUser) {
        if (id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1、校验队伍是否存在
        Team team = getTeamById(id);
        //2、校验是否是队长
        if (team.getUserId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //3、删除所有加入队伍的信息
        Long teamId = team.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean flag = userTeamService.remove(queryWrapper);
        if (!flag){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //4、删除队伍
        return this.removeById(teamId);
    }

    /**
     * 修改
     * @param teamUpdateRequest
     * @param longinUser 用户信息
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User longinUser) {
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        if (team.getUserId() != longinUser.getId()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不是本人");
        }
//        String password = teamUpdateRequest.getPassword();
        TeamStatesEnum teamStatesEnum = TeamStatesEnum.getEnumTeam(teamUpdateRequest.getStatus());
        if (teamStatesEnum.equals(TeamStatesEnum.SECRET)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请设置密码");
            }
        }
        BeanUtils.copyProperties(teamUpdateRequest,team);
        boolean result = this.updateById(team);
        if (!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return true;
    }

    /**
     * 查询全部
     * @param teamQueryDTO
     * @return
     */
    @Override
    public List<TeamUserVO> listTeam(TeamQueryDTO teamQueryDTO) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQueryDTO != null) {
            long id = teamQueryDTO.getId();
            //根据队伍id查询
            if (id > 0) {
                queryWrapper.eq("id", id);
            }
            //根据队伍名称查询
            String teamName = teamQueryDTO.getTeamName();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("teamName", teamName);
            }
            //根据队伍名称查询
            String teamDescription = teamQueryDTO.getTeamDescription();
            if (StringUtils.isNotBlank(teamDescription)) {
                queryWrapper.like("teamDescription", teamDescription);
            }
            //根据最大人数查询
            Integer maxNum = teamQueryDTO.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            //根据队伍状态查询
            Integer status = teamQueryDTO.getStatus();
            if (status != null && status > -1) {
                queryWrapper.eq("states", status);
            }
            long userId = teamQueryDTO.getUserId();
            if (userId > 0){
                queryWrapper.eq("userId", userId);
            }
        }
        //不展示过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
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

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser) {
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatesEnum teamStatesEnum = TeamStatesEnum.getEnumTeam(status);
        if (TeamStatesEnum.PRIVATE.equals(teamStatesEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        String teamPassword = teamJoinRequest.getTeamPassword();
        if (TeamStatesEnum.SECRET.equals(teamStatesEnum)){
            if (StringUtils.isBlank(teamPassword) || !teamPassword.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不正确");
            }
        }
        //最多加入5个队伍
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasJoin = userTeamService.count(queryWrapper);
        if (hasJoin > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
        }
        //不能加入同一个队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("teamId",teamId);
        long count = userTeamService.count(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复加入队伍");
        }
        //队伍已加入的人数
        long joinPeople = userJoinNum(teamId);
        if (joinPeople >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        Long userId = loginUser.getId();
        UserTeam queryuserTeam = new UserTeam();
        queryuserTeam.setUserId(userId);
        queryuserTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryuserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"您并未加入队伍");
        }
        //判断队伍的人数
        long joinNum = userJoinNum(teamId);
        if (joinNum == 1){
            //队伍信息
            this.removeById(teamId);
        }else {
            //判断是否是队长
            if (team.getUserId() == userId){
                //把队长权限转移为第二人
                //1/查询已加入队伍的所有用户和加入时间，去id靠前的两条数据
                QueryWrapper<UserTeam> queryWrapperList = new QueryWrapper<>();
                queryWrapperList.eq("teamId",teamId);
                queryWrapperList.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapperList);
                if (CollectionUtils.isEmpty(userTeamList)){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long updateUserId = nextUserTeam.getUserId();
                //更新当前队长的职务
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(updateUserId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        //移除关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 根据id获取用户信息
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        return team;
    }

    /**
     * 获取队伍的当前人数
     * @param teamId
     * @return
     */
    private long userJoinNum(long teamId){
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        return userTeamService.count(queryWrapper);
    }
}




