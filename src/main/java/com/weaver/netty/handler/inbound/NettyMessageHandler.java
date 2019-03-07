package com.weaver.netty.handler.inbound;

import com.weaver.OfflineMessage.OfflineMessageStrategy;
import com.weaver.manager.session.SessionManager;
import com.weaver.netty.Interceptor.InterceptorManager;
import com.weaver.netty.RouteTable;
import com.weaver.netty.stanza.JID;
import com.weaver.netty.stanza.Message;
import com.weaver.netty.stanza.Stanza;
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
 * @Date: 2018/12/29 10:14
 * @Version 1.0
 */
@Component
public class NettyMessageHandler extends SimpleChannelInboundHandler<Message> {

    private static NettyMessageHandler nettyMessageHandler;

    @Autowired
    private InterceptorManager interceptorManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private OfflineMessageStrategy messageStrategy;

    @Autowired
    private RouteTable routeTable;

    public NettyMessageHandler(){}

    @PostConstruct
    public void init(){
        nettyMessageHandler = this;
        nettyMessageHandler.interceptorManager = interceptorManager;
        nettyMessageHandler.sessionManager = sessionManager;
        nettyMessageHandler.messageStrategy = messageStrategy;
        nettyMessageHandler.routeTable = routeTable;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        JID from = msg.getFrom();

        LocalClientSession session = nettyMessageHandler.sessionManager.getSession(msg.getFrom());

        Attribute<Boolean> write = ctx.attr(Attr.WRITE);
        Attribute<Boolean> read = ctx.attr(Attr.READ);
        write.setIfAbsent(true);
        read.setIfAbsent(false);
        //nettyMessageHandler.sessionManager.getSession();
        nettyMessageHandler.interceptorManager.invokeInterceptors(msg,session,read.get(),write.get());

        JID recipientJID = msg.getTo();

        nettyMessageHandler.routeTable.routePacket(recipientJID, msg, false);

        msg.setTo(session.getAddress());
        msg.setFrom((JID)null);

        boolean isAcceptable = true;

        if (isAcceptable){
            nettyMessageHandler.routeTable.routePacket(recipientJID,msg,false);
        }

        write.setIfAbsent(true);
        read.setIfAbsent(true);
        nettyMessageHandler.interceptorManager.invokeInterceptors(msg,session,read.get(),write.get());

    }

    public void routingFailed(JID recipient, Stanza packet){
        boolean storeOffline = true;

        if (!storeOffline){

        }else {
            messageStrategy.storeOffline( (Message) packet );
        }
    }
}