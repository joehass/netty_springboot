package com.weaver.netty.handler.inbound;

import com.weaver.authentication.SASLAuthentication;
import com.weaver.manager.session.SessionManager;
import com.weaver.netty.xml.XMLElement;
import com.weaver.netty.xml.XMLElementImpl;
import com.weaver.session.LocalClientSession;
import com.weaver.staticconst.Attr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ClassName: MyServerHandler
 * Author:   胡烨
 * Date:     2018/10/26 16:45
 * Description:
 * History:
 *
 * @Version: 1.0
 */
@Component
public class MyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static MyServerHandler myServerHandler;

    private static final String FRAMING_NAMESPACE = "urn:ietf:params:xml:ns:xmpp-framing";

    //认证状态
    private SASLAuthentication.Status saslStatus;
    public MyServerHandler (){}

    @Autowired
    SessionManager sessionManager;

    private LocalClientSession xmppSession;

    @PostConstruct
    public void init(){
        myServerHandler = this;
        myServerHandler.sessionManager = sessionManager;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        System.out.println("MyServerHandler的channel：" + ctx.channel().toString());
        XMLElement xmlElement = XMLElementImpl.fromString(msg.text());

        if (xmppSession == null){
            initiateSession(xmlElement,ctx);
        }
        else {
            Attribute<LocalClientSession> session = ctx.attr(Attr.SESSION);
            session.setIfAbsent(xmppSession);
            ctx.fireChannelRead(xmlElement);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接开始");
        System.out.println("连接开始的channel: " + ctx.channel().toString());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("111111111122222"));
}

    private void initiateSession(XMLElement msg,ChannelHandlerContext ctx){
        xmppSession = myServerHandler.sessionManager.createClientSession(ctx);
        xmppSession.setSessionData("ws",Boolean.TRUE);

        if (xmppSession == null){

        }else {
            openStream(msg.getAttribute("from"),ctx);
            configureStream(ctx);
        }


    }

    private void configureStream(ChannelHandlerContext ctx) {

        StringBuilder sb = new StringBuilder(250);

        sb.append("<stream:features xmlns:stream='http://etherx.jabber.org/streams'><mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"><mechanism>PLAIN</mechanism><mechanism>SCRAM-SHA-1</mechanism><mechanism>CRAM-MD5</mechanism><mechanism>DIGEST-MD5</mechanism></mechanisms><auth xmlns='http://jabber.org/features/iq-auth'/>");

        deliver(sb.toString(),ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

    private void openStream(String jid,ChannelHandlerContext ctx){
        //xmppSession.incrementClientPacketCount();
        StringBuilder sb = new StringBuilder(250);
        sb.append("<open ");
        sb.append("from='").append("weaver").append("' ");
        sb.append("id='").append(xmppSession.getStreamID().getID()).append("' ");
        sb.append("xmlns='").append(FRAMING_NAMESPACE).append("' ");
        sb.append("xml:lang='").append("en").append("' ");
        sb.append("version='1.0'/>");
        deliver(sb.toString(),ctx);
    }

    void deliver(String packet,ChannelHandlerContext ctx){
        ctx.channel().writeAndFlush(new TextWebSocketFrame(packet));
    }
}
