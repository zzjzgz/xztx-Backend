package xyz.zzj.springbootxztxbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import xyz.zzj.springbootxztxbackend.connon.BaseResponse;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.connon.ResultUtils;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.model.domain.Team;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.UserTeam;
import xyz.zzj.springbootxztxbackend.model.domain.dto.TeamQueryDTO;
import xyz.zzj.springbootxztxbackend.model.domain.request.*;
import xyz.zzj.springbootxztxbackend.model.domain.vo.TeamUserVO;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.service.TeamService;
import xyz.zzj.springbootxztxbackend.service.UserService;
import xyz.zzj.springbootxztxbackend.service.UserTeamService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.controller
 * @ClassName: TeamController
 * @Author: zengz
 * @CreateTime: 2024/2/5 10:20
 * @Description: 控制类
 * @Version: 1.0
 */

@RestController
@RequestMapping("/team")
//这个是线上用于跨域的，本地请注释其注解，//上线记得改服务器地址
//@CrossOrigin(origins = {"http://localhost:5173/"},allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;


    /**
     * 增加队伍
     * @param teamAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team,loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 解散队伍
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean flag = teamService.deleteTeam(deleteRequest.getId(),loginUser);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (teamUpdateRequest.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean flag = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 查询个人信息
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 查询全部
     * @param teamQueryDTO 查询全部的dto
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeam(TeamQueryDTO teamQueryDTO,HttpServletRequest request){
        if (teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        List<TeamUserVO> teamList = teamService.listTeam(teamQueryDTO);
        List<UserTeam> userTeamList = userTeamService.list();
        Map<Long, List<UserTeam>> collect = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId, Collectors.toList()));
        //判断当前用户是否以加入队伍
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            queryWrapper.eq("userId",loginUser.getId());
            queryWrapper.in("teamId",teamIdList);
            //已加入的队伍
            userTeamList = userTeamService.list(queryWrapper);
            //已加入的队伍id集合
            Set<Long> hasJoinTeamId = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team ->{
                boolean hasJoin = hasJoinTeamId.contains(team.getId());
                team.setHasJoin(hasJoin);
                team.setHasJoinNum((long) collect.get(team.getId()).size());
            });
        }catch (Exception e){}
        return ResultUtils.success(teamList);
    }

    /**
     * 分页查询
     * @param teamQueryDTO 查询全部的dto
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamPage(TeamQueryDTO teamQueryDTO){
        if (teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        Page<Team> listTeamPage = teamService.listTeamPage(teamQueryDTO);
        return ResultUtils.success(listTeamPage);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean flag = teamService.joinTeam(teamJoinRequest,loginUser);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean flag = teamService.quitTeam(teamQuitRequest,loginUser);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     * @param teamQueryDTO 查询全部的dto
     * @return
     */
    @GetMapping("/list/myCreate")
    public BaseResponse<List<TeamUserVO>> getUserCreateTeam(TeamQueryDTO teamQueryDTO,HttpServletRequest request){
        if (teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        //绑定当前用户id
        teamQueryDTO.setUserId(loginUser.getId());
        List<TeamUserVO> list = teamService.listTeam(teamQueryDTO);
        return ResultUtils.success(list);
    }

    /**
     * 获取我加入的队伍
     * @param teamQueryDTO 查询全部的dto
     * @return
     */
    @GetMapping("/list/myJoin")
    public BaseResponse<List<TeamUserVO>> getUserJoinTeam(TeamQueryDTO teamQueryDTO,HttpServletRequest request){
        if (teamQueryDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        if (CollectionUtils.isEmpty(userTeamList)){
            return ResultUtils.success(new ArrayList<>());
        }
        List<Long> listId = new ArrayList<>(userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId)).keySet());
        teamQueryDTO.setListId(listId);
        List<TeamUserVO> list = teamService.listTeam(teamQueryDTO);
        return ResultUtils.success(list);
    }

    @PostMapping("/info")
    public BaseResponse<List<UserVO>> joinTeamUserInfo(@RequestBody DeleteRequest deleteRequest){
        //判断参数是否为空
        if (deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserVO> userVOList = teamService.joinTeamUserInfo(deleteRequest);
        return ResultUtils.success(userVOList);
    }
}
