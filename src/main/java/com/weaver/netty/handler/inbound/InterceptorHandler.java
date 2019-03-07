package com.weaver.netty.handler.inbound;

import com.weaver.netty.stanza.Stanza;
import com.weaver.redis.RedisUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: 胡烨
 * @Date: 2018/12/27 14:55
 * @Version 1.0
 */
@Component
public class InterceptorHandler extends SimpleChannelInboundHandler<Stanza> {

    private static InterceptorHandler interceptorHandler;

    @Autowired
    private RedisUtil redisUtil;

    public InterceptorHandler(){}

    @PostConstruct
    public void init(){
        interceptorHandler = this;
        interceptorHandler.redisUtil = this.redisUtil;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Stanza msg) throws Exception {


    }
}
