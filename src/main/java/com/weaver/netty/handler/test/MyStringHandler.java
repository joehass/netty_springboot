package com.weaver.netty.handler.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class MyStringHandler extends SimpleChannelInboundHandler<String> {

    public final static AttributeKey<String> NETTY_CHANNEL_KEY = AttributeKey.valueOf("msg");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("开始调用MyStringHandler");
        String msg1 = msg + "222";
        Attribute<String> attr = ctx.attr(NETTY_CHANNEL_KEY);

        attr.setIfAbsent(msg1);
        String str = attr.get();

        System.out.println("str11= " + str);
        ctx.channel().write(msg);
    }
}
