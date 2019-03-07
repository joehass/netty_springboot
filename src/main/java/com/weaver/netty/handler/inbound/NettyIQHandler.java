package com.weaver.netty.handler.inbound;

import com.weaver.netty.stanza.IQ;
import com.weaver.plugin.handler.IQHandler;
import com.weaver.route.IQRoute;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class NettyIQHandler extends SimpleChannelInboundHandler<IQ> {

    public static NettyIQHandler nettyIQHandler;

    @Autowired
    private IQRoute iqRoute;

    public NettyIQHandler(){
    }

    @PostConstruct
    public void init(){
        nettyIQHandler = this;
        nettyIQHandler.iqRoute = this.iqRoute;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IQ msg) throws Exception {
        System.out.println("NettyIQHandler的channel：" + ctx.channel().toString());
        String namespace = msg.getXML().getNamespace();

        if (namespace != null) {
            IQHandler handler = nettyIQHandler.iqRoute.getHandler(namespace);
            IQ reply = handler.process(msg);

            if (reply != null) {
                ctx.channel().writeAndFlush(reply);
            }
        }
    }


}
