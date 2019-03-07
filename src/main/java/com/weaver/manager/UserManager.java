package com.weaver.manager;

import com.weaver.mapper.OfUserMapper;
import com.weaver.netty.stanza.JID;
import com.weaver.pojo.OfUser;
import com.weaver.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 胡烨
 * @Date: 2018/12/28 13:51
 * @Version 1.0
 */
@Component
public class UserManager implements Serializable {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OfUserMapper userMapper;

    public OfUser getUserByUserName(String name){
        List<OfUser> users = redisUtil.getObjectList("User", name);

        for (OfUser user : users){
            if (user.getUsername().equals(name)){
                return user;
            }
        }
        return null;
    }

    public boolean isLocal(JID jid){
        boolean local = false;

        if (jid != null && "weaver" != null && "weaver".equals(jid.getDomain())){
            local = true;
        }
        return local;
    }

    public boolean isRegisteredUser(JID user) {
        if (isLocal(user)) {
            OfUser ofUser = getUserByUserName(user.getNode());
            if (ofUser != null) {
                return true;
            } else
                return false;
        }

        return true;
    }

    public OfUser loadUser(String username){
        if (username.contains("@")){
            username = username.substring(0,username.lastIndexOf("@"));
        }

        OfUser ofUser = userMapper.LOAD_USER(username);

        return ofUser;
    }
}
