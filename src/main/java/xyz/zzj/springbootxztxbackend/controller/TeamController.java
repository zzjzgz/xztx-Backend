package xyz.zzj.springbootxztxbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import xyz.zzj.springbootxztxbackend.connon.BaseResponse;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.connon.ResultUtils;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.model.domain.Team;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.dto.TeamQueryDTO;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamAddRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamJoinRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamQuitRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamUpdateRequest;
import xyz.zzj.springbootxztxbackend.model.domain.vo.TeamUserVO;
import xyz.zzj.springbootxztxbackend.service.TeamService;
import xyz.zzj.springbootxztxbackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.controller
 * @ClassName: TeamController
 * @Author: zengz
 * @CreateTime: 2024/2/5 10:20
 * @Description: TODO 描述类的功能
 * @Version: 1.0
 */

@RestController
@RequestMapping("/team")
//这个是线上用于跨域的，本地请注释其注解
@CrossOrigin(origins = {"http://localhost:5173/"},allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

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
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id,HttpServletRequest request){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean flag = teamService.deleteTeam(id,loginUser);
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
    public BaseResponse<List<TeamUserVO>> listTeam(TeamQueryDTO teamQueryDTO){
        List<TeamUserVO> list = teamService.listTeam(teamQueryDTO);
        return ResultUtils.success(list);
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


}
