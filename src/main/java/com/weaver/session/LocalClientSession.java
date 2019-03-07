package com.weaver.session;

import com.weaver.authentication.AuthToken;
import com.weaver.component.listener.PresenceEventDispatcher;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Presence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 胡烨
 * @Date: 2019/1/3 9:50
 * @Version 1.0
 */
@Component
public class LocalClientSession extends LocalSession implements Serializable {

    /**
     * 被认证过的session地址
     */
    private JID address;

    private Presence presence;

    @Autowired
    private PresenceEventDispatcher presenceEventDispatcher;

    private long clientPacketCount = 0;
    private long serverPacketCount = 0;
    private long lastActiveDate;

    protected AuthToken authToken;

    private final Map<String,Object> sessionData = new HashMap<>();

    public JID getAddress() {
        return address;
    }

    public void setAddress(JID address) {
        this.address = address;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    /**
     * 增加从客户端发往服务器的包数量
     */
    public void incrementClientPacketCount(){
        clientPacketCount++;
        lastActiveDate = System.currentTimeMillis();

    }

    /**
     * 将presence设置到session中
     * @param presence
     */

    public void setPresence1(Presence presence){
        presenceEventDispatcher.availableSession(this,presence);
    }

    /**
     * 保存临时会话
     * @param key
     * @param value
     */
    public void setSessionData(String key,Object value){
        synchronized (sessionData){
            sessionData.put(key,value);
        }
    }

    public Object getSessionData(String key){
        synchronized (sessionData){
            return sessionData.get(key);
        }
    }

    public void removeSessionData(String key) {
        synchronized (sessionData) {
            sessionData.remove(key);
        }
    }


    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
