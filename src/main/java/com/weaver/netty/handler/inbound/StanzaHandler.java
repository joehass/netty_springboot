package com.weaver.netty.handler.inbound;

import com.weaver.authentication.SASLAuthentication;
import com.weaver.manager.session.SessionManager;
import com.weaver.netty.stanza.*;
import com.weaver.netty.xml.XMLElement;
import com.weaver.route.IQRoute;
import com.weaver.session.LocalClientSession;
import com.weaver.staticconst.Attr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StanzaHandler extends SimpleChannelInboundHandler<XMLElement> {

    public static StanzaHandler stanzaHandler;

    private static final String STREAM_HEADER = "open";
    private static final String STREAM_FOOTER = "close";
    private static final String FRAMING_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-framing";

    //是否开启验证
    private boolean startedSASL = false;

    @Autowired
    private IQRoute iqRoute;

    @Autowired
    private LocalClientSession session;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SASLAuthentication saslAuthentication;

    private SASLAuthentication.Status saslStatus;

    public StanzaHandler(){}

    @PostConstruct
    public void init(){
        stanzaHandler = this;
        stanzaHandler.iqRoute = this.iqRoute;
        stanzaHandler.sessionManager = this.sessionManager;
        stanzaHandler.session = this.session;
        stanzaHandler.saslAuthentication = this.saslAuthentication;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XMLElement msg) throws Exception {

        String tagName = msg.getTagName();

        if ("iq".equals(tagName)){

            XMLElement query = msg.getFirstChild("query");
            IQ iq = getIQ(msg);
            route(iq);
            if (stanzaHandler.session != null){
                iq.setFrom(stanzaHandler.session.getAddress());
            }
            ctx.fireChannelRead(iq);
        }

        if ("presence".equals(tagName)){
            Presence presence = new Presence(msg);
            //route(presence);
            Attribute<Boolean> write = ctx.attr(Attr.WRITE);
            Attribute<Boolean> read = ctx.attr(Attr.READ);
            write.setIfAbsent(false);
            read.setIfAbsent(true);
            ctx.fireChannelRead(presence);
        }

        if ("message".equals(tagName)){
            Message message = new Message(msg);
            route(message);
            ctx.fireChannelRead(message);
        }
    }


    private IQ getIQ(XMLElement doc){
        XMLElement query = doc.getFirstChild("query");
        IQ iq = null;
        if (query != null){
          iq = new IQ(doc);
        }else {
            iq = new IQ(doc);
        }

        return iq;
    }


    public void route(Stanza packet){

        JID from = packet.getFrom();
        stanzaHandler.session.setAddress(from);
    }
}
