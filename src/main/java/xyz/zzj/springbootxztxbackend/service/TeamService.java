package xyz.zzj.springbootxztxbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.zzj.springbootxztxbackend.model.domain.Team;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import xyz.zzj.springbootxztxbackend.model.domain.dto.TeamQueryDTO;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamJoinRequest;
import xyz.zzj.springbootxztxbackend.model.domain.request.TeamUpdateRequest;
import xyz.zzj.springbootxztxbackend.model.domain.vo.TeamUserVO;

import java.util.List;

/**
* @author zengz
* @description 针对表【team】的数据库操作Service
* @createDate 2024-02-04 22:28:42
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);

    boolean deleteTeam(long id);


    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User longinUser);

    List<TeamUserVO> listTeam(TeamQueryDTO teamQueryDTO);

    Page<Team> listTeamPage(TeamQueryDTO teamQueryDTO);

    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);
}
