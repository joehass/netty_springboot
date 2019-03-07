package com.weaver.iqhandler;

import com.weaver.manager.session.SessionManager;
import com.weaver.netty.stanza.IQ;
import com.weaver.plugin.handler.IQHandler;
import com.weaver.session.LocalClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: 胡烨
 * @Date: 2019/1/6 15:52
 * @Version 1.0
 */

/**
 * 绑定资源号
 */
@Component
public class IQBindHandler extends IQHandler {

    private String moduleName = "urn:ietf:params:xml:ns:xmpp-bind";

    @Autowired
    private SessionManager sessionManager;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public IQ handleIQ(IQ packet) {
        LocalClientSession session = (LocalClientSession) sessionManager.getSession(packet.getFrom());
        IQ reply = IQ.createResultIQ(packet);
        reply.setChildElement("bind", "urn:ietf:params:xml:ns:xmpp-bind");
        packet.getChildElement();

        return reply;
    }
}