package com.weaver;

import com.weaver.manager.session.SessionManager;
import com.weaver.netty.stanza.Stanza;
import com.weaver.session.LocalClientSession;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: 胡烨
 * @Date: 2019/1/20 15:23
 * @Version 1.0
 */
@Component
public class InternalComponet {

    @Autowired
    private SessionManager sessionManager;

    public void sendPacket(Stanza packet){
        LocalClientSession session = sessionManager.getSession(packet.getFrom());

        session.getChannel().writeAndFlush(new TextWebSocketFrame(packet.toString()));
    }
}
