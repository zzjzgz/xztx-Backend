package xyz.zzj.springbootxztxbackend.constant;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.constant
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-09  19:23
 * @Description: 用户常量
 * @Version: 1.0
 *
 * 用户常量
 */
public interface UserConstant {

    /**
     * 保存一个登录态
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 用户角色
     */
    int DEFAULT_ROLE = 0;


    /**
     * 管理员角色
     */
    int ADMIN_ROLE = 1;

    /**
     * 主页信息key
     */
    String RECOMMEND_KEY_PREFIX = "xztx:user:recommend:";
    /**
     * 预加载key
     */
    String JOIN_KEY_PREFIX = "xztx:team:joinTeam:";
    /**
     * 加入队伍
     */
    String PRECACHE_KEY_PREFIX = "xztx:precache:doCache:";

    /**
     * 对密码进行加盐，混淆
     */
   String S_ALT = "zzj";

}

