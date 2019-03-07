package com.weaver.netty.handler.inbound;

import com.weaver.manager.session.SessionManager;
import com.weaver.netty.xml.XMLElement;
import com.weaver.redis.RedisUtil;
import com.weaver.session.LocalClientSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 初始化session
 */
public class SessionHandler extends SimpleChannelInboundHandler<XMLElement> {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    SessionManager sessionManager;

    private LocalClientSession xmppSession;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, XMLElement msg) throws Exception {

        ctx.fireChannelRead(msg);
    }
}
