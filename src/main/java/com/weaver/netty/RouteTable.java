package com.weaver.netty;

import com.weaver.manager.session.SessionManager;
import com.weaver.netty.handler.inbound.NettyMessageHandler;
import com.weaver.netty.stanza.*;
import com.weaver.session.LocalClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: 胡烨
 * @Date: 2019/1/28 15:07
 * @Version 1.0
 */
@Component
public class RouteTable {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private NettyMessageHandler NettyMessageHandler;

    public void routePacket(JID jid, Stanza packet,boolean fromServer){
        boolean routed = false;
        if ("weaver".equals(jid.getDomain())){
            routed = routeToLocalDomain(jid, packet, fromServer);
        }

        //接收方不在线
        if (!routed){
            if (packet instanceof IQ){

            }else if (packet instanceof Message){
                NettyMessageHandler.routingFailed(jid,packet);
            }else if (packet instanceof Presence){

            }
        }
    }

    /**
     * 将数据包发送到本地服务器
     * @param jid
     * @param packet
     * @param fromServer
     * @return
     */
    private boolean routeToLocalDomain(JID jid,Stanza packet,boolean fromServer){
        boolean routed = false;
        LocalClientSession session = sessionManager.getSession(jid);

        if (jid.getResource() == null){

        }else {

        }

        if (session != null){
            routed = true;
        }
        return routed;

    }
}
