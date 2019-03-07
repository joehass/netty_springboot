package com.weaver.netty.handler.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

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
public class TestMyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){

        System.out.println("开始调用TestMyServerHandler");
        System.out.println("msg= " + msg.text());
        String msg1 = msg.text() + "111";

        ctx.fireChannelRead(msg1);
        ChannelHandlerContext read = ctx.read();
        System.out.println("msg222= " + msg.text());
        System.out.println("返回调用TestMyServerHandler");
        ctx.channel().write(msg1);
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接开始");
        System.out.println("连接开始的channel: " + ctx.channel().toString());
    }
}
