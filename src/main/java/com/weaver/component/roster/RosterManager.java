package com.weaver.component.roster;

/**
 * @Author: 胡烨
 * @Date: 2019/1/12 13:49
 * @Version 1.0
 */

import com.weaver.manager.session.SessionManager;
import com.weaver.redis.RedisUtil;
import com.weaver.staticconst.Attr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 一种简单的服务，允许组件仅根据所有者的ID检索花名册。
 * 用户可以使用方便的方法获取与所有者关联的花名册。但是，
 * 有许多组件需要仅基于通用ID所有者密钥检索花名册。
 * 这个接口定义了一个可以做到这一点的服务。
 * 这允许那些为资源所有者一般性地管理资源的类（例如状态更新）一般性地提供服务，
 * 而不知道或关心花名册所有者是用户、聊天机器人等。
 */
@Component
public class RosterManager {

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public SessionManager sessionManager;

    public Roster getRoster(String username){

        Roster roster = (Roster) redisUtil.getSingleObjectHash(Attr.ROSTER,username);

        if (roster.getSessionManager() == null){
            roster = Roster.getInstance();
            roster.setUsername(username);
            roster.setSessionManager(sessionManager);
        }
        return roster;
    }
}
