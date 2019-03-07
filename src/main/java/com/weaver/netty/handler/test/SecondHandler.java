package com.weaver.netty.handler.test;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class SecondHandler extends ChannelDuplexHandler {

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {

        System.out.println("SecondHandler read");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println();
    }
}
