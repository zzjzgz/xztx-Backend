package xyz.zzj.springbootxztxbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import xyz.zzj.springbootxztxbackend.connon.BaseResponse;
import xyz.zzj.springbootxztxbackend.connon.ErrorCode;
import xyz.zzj.springbootxztxbackend.connon.ResultUtils;
import xyz.zzj.springbootxztxbackend.exception.BusinessException;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.dto.FriendAddDTO;
import xyz.zzj.springbootxztxbackend.model.domain.request.DeleteRequest;
import xyz.zzj.springbootxztxbackend.model.domain.vo.UserVO;
import xyz.zzj.springbootxztxbackend.model.domain.xztxEnum.RelationshipStatusEnum;
import xyz.zzj.springbootxztxbackend.service.UserFriendsService;
import xyz.zzj.springbootxztxbackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @BelongsPackage: xyz.zzj.springbootxztxbackend.controller
 * @ClassName: UserFriendsController
 * @Author: zengz
 * @CreateTime: 2024/4/18 9:24
 * @Description: 好友
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user/friends")
@Slf4j
public class UserFriendsController {

    @Resource
    private UserService userService;

    @Resource
    private UserFriendsService userFriendsService;

    @GetMapping("")
    public BaseResponse<List<UserVO>> getFriends(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        //查找好友数
        List<UserVO> userVOList = userFriendsService.getFriends(null,loginUser, RelationshipStatusEnum.SUCCESS.getStatus());
        if (CollectionUtils.isEmpty(userVOList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前无好友");
        }
        return ResultUtils.success(userVOList);
    }
    @GetMapping("/search")
    public BaseResponse<List<UserVO>> getFriendsSearch(String name,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        List<UserVO> userVOList = userFriendsService.getFriends(name,loginUser,RelationshipStatusEnum.SUCCESS.getStatus());
        if (CollectionUtils.isEmpty(userVOList)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(userVOList);
    }

    @PostMapping("/add")
    public BaseResponse<Integer> addFriends(@RequestBody FriendAddDTO friendAddDTO, HttpServletRequest request){
        if (friendAddDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        Long userId = loginUser.getId();
        long friendId = friendAddDTO.getFriendId();
        int id = userFriendsService.addFriends(userId, friendId);
        return ResultUtils.success(id);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFriend(@RequestBody DeleteRequest deleteRequest){
        if (deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        boolean flag = userFriendsService.deleteFriend(id);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }


    @GetMapping("/apply/num")
    public BaseResponse<Long> friendApplyNum(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        Long userId = loginUser.getId();
        Long applyNum = userFriendsService.getApplyFriendNum(userId);
        return ResultUtils.success(applyNum);
    }

    @GetMapping ("/apply")
    public BaseResponse<List<UserVO>> friendApply(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        List<UserVO> userVOList =  userFriendsService.getApplyFriends(loginUser,RelationshipStatusEnum.APPLY.getStatus());
        if (CollectionUtils.isEmpty(userVOList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前无申请人");
        }
        return ResultUtils.success(userVOList);
    }

    @PostMapping("agreeFriend")
    public BaseResponse<Boolean> agreeFriend(@RequestBody FriendAddDTO friendAddDTO,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (friendAddDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        Long friendId = loginUser.getId();
        //将获取的id作为好友发起人id
        long userId = friendAddDTO.getFriendId();
        boolean flag = userFriendsService.agreeFriend(userId,friendId);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }

    @PostMapping("refuseFriend")
    public BaseResponse<Boolean> refuseFriend(@RequestBody FriendAddDTO friendAddDTO,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        if (friendAddDTO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long friendId = loginUser.getId();
        //将获取的id作为好友发起人id
        long userId = friendAddDTO.getFriendId();
        boolean flag = userFriendsService.refuseFriend(userId,friendId);
        if (!flag){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(true);
    }
}
