package com.weaver.manager.session;

/**
 * @Author: 胡烨
 * @Date: 2019/1/3 10:23
 * @Version 1.0
 */

import com.weaver.manager.UserChannelManager;
import com.weaver.netty.stanza.JID;
import com.weaver.session.LocalClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 管理当地的session，不与集群通信
 */
@Component
public class LocalSessionManager {


    @Autowired
    private UserChannelManager userChannelManager;

   public void setPreAuthenticatedSessions(LocalClientSession session){
       userChannelManager.put(session);
   }

   public LocalClientSession getPreAuthenticatedSessions(JID key){
      return userChannelManager.get(key);
   }

   public List<LocalClientSession> getAllSession() {
       return userChannelManager.getAll();
    }
}
