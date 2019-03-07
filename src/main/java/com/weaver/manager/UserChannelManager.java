package com.weaver.manager;

/**
 * @Author: 胡烨
 * @Date: 2019/1/4 15:20
 * @Version 1.0
 */

import com.weaver.netty.stanza.JID;
import com.weaver.session.LocalClientSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Component
public class UserChannelManager {

    private Map<String, LocalClientSession> userChannel = new ConcurrentHashMap<>();

    public void put(LocalClientSession session){
        userChannel.put(session.getAddress().toString(),session);
    }

    public LocalClientSession get(JID key){

        return  userChannel.get(key.toString());
    }

    public List<LocalClientSession> getAll(){
        List<LocalClientSession> list = new ArrayList();
        userChannel.forEach((k,v) -> {
            list.add(v);
        });

        return list;
    }
}
