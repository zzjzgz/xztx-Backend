package xyz.zzj.springbootxztxbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.zzj.springbootxztxbackend.model.domain.Team;
import xyz.zzj.springbootxztxbackend.service.TeamService;
import xyz.zzj.springbootxztxbackend.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author zengz
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-02-04 22:28:42
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




