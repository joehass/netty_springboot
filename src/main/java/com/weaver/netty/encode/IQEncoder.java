package com.weaver.netty.encode;

import com.weaver.netty.stanza.Stanza;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class IQEncoder extends MessageToMessageEncoder<Stanza> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Stanza msg, List<Object> out) throws Exception {

        System.out.println("myencode invoke");
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg.toString());
        out.add(textWebSocketFrame);
    }
}
