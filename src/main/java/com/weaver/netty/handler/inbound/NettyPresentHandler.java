package com.weaver.netty.handler.inbound;

import com.weaver.component.handler.PresenceUpdateHandler;
import com.weaver.manager.session.SessionManager;
import com.weaver.netty.Interceptor.InterceptorManager;
import com.weaver.netty.stanza.Presence;
import com.weaver.redis.RedisUtil;
import com.weaver.session.LocalClientSession;
import com.weaver.staticconst.Attr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class NettyPresentHandler extends SimpleChannelInboundHandler<Presence> {

    private static NettyPresentHandler nettyPresentHandler;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private InterceptorManager interceptorManager;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private LocalClientSession session;

    @Autowired
    private PresenceUpdateHandler presenceUpdateHandler;

    public NettyPresentHandler(){}

    @PostConstruct
    public void init(){
        nettyPresentHandler = this;
        nettyPresentHandler.redisUtil = this.redisUtil;
        nettyPresentHandler.interceptorManager = this.interceptorManager;
        nettyPresentHandler.sessionManager = this.sessionManager;
        nettyPresentHandler.presenceUpdateHandler = this.presenceUpdateHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Presence msg) throws Exception {

        if (msg == null){
            throw new NullPointerException();
        }
        session = nettyPresentHandler.sessionManager.getSession(msg.getFrom());

        if (session == null)
            handle(msg);

        Attribute<Boolean> write = ctx.attr(Attr.WRITE);
        Attribute<Boolean> read = ctx.attr(Attr.READ);
        write.setIfAbsent(true);
        read.setIfAbsent(false);
        nettyPresentHandler.interceptorManager.invokeInterceptors(msg,session,read.get(),write.get());

        handle(msg);
    }

    private void handle(Presence packet){
        nettyPresentHandler.presenceUpdateHandler.process(packet);
    }
}
