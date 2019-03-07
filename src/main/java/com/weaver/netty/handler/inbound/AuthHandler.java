package com.weaver.netty.handler.inbound;

import com.weaver.authentication.SASLAuthentication;
import com.weaver.netty.xml.XMLElement;
import com.weaver.session.LocalClientSession;
import com.weaver.staticconst.Attr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: 胡烨
 * @Date: 2019/2/18 9:10
 * @Version 1.0
 */
@Component
public class AuthHandler extends SimpleChannelInboundHandler<XMLElement> {

    public static AuthHandler authHandler;

    //是否开启验证
    private boolean startedSASL = false;

    private static final String STREAM_HEADER = "open";
    private static final String STREAM_FOOTER = "close";
    private static final String FRAMING_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-framing";


    @Autowired
    private SASLAuthentication saslAuthentication;

    private SASLAuthentication.Status saslStatus;

    @Autowired
    private LocalClientSession session;

    @PostConstruct
    public void init(){
        authHandler = this;
        authHandler.session = this.session;
        authHandler.saslAuthentication = this.saslAuthentication;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XMLElement msg) throws Exception {
        String tagName = msg.getTagName();
        Attribute<LocalClientSession> session = ctx.attr(Attr.SESSION);
        LocalClientSession localClientSession = session.get();

        if ("auth".equals(tagName)){
            //开启有SASL验证
            startedSASL = true;
            authHandler.session.incrementClientPacketCount();
            saslStatus = authHandler.saslAuthentication.handle(localClientSession,msg);
        }

        if (startedSASL && "response".equals(tagName) || "abort".equals(tagName)){
            saslStatus = authHandler.saslAuthentication.handle(localClientSession,msg);
        }

        if (SASLAuthentication.Status.authenticated.equals(saslStatus)){
            ctx.fireChannelRead(msg);
        }
    }
}
