package xyz.zzj.springbootxztxbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.zzj.springbootxztxbackend.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zeng
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-01-08 16:50:47
* @Entity xyz.zzj.springbootusercenter.model.domain.User
*/

@Mapper
public interface UserMapper extends BaseMapper<User> {

}




