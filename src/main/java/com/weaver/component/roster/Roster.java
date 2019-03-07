package com.weaver.component.roster;

/**
 * @Author: 胡烨
 * @Date: 2019/1/12 13:05
 * @Version 1.0
 */

import com.weaver.manager.session.SessionManager;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Presence;
import com.weaver.session.LocalClientSession;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * 名册是一系列在线用户的集合，名册在im客户端是一群相似的伙伴
 * 该类代表了这些名册数据
 *
 * 及时有效的更新用户的的名册。为了达到这个目的，该类会自动的更新已经存储的名册，同时
 * 发出消息对所有已经登陆的session
 */
@Component
public class Roster implements Serializable {

    @Autowired
    private SessionManager sessionManager;
    private String username;

    private static volatile Roster roster;

    public static synchronized Roster getInstance(){
        if (roster == null){
            synchronized (Roster.class){
                if (roster == null){
                    roster = new Roster();
                }
            }
        }
        return roster;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 广播presence消息给所有的订阅者
     * 任何presence改变都会被广播给所有订阅者
     * @param packet
     */
    public void broadcastPresence(Presence packet){
        JID from = packet.getFrom();
        if (from != null){
            LocalClientSession session = sessionManager.getSession(from);
            if (session != null){
                List<LocalClientSession> allSession = sessionManager.getAllSession();
                allSession.forEach(localClientSession ->{
                    localClientSession.getChannel().writeAndFlush(new TextWebSocketFrame(packet.toString()));
                } );
            }
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}