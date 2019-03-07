package com.weaver.netty;


import com.weaver.netty.handler.inbound.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * ClassName: MyServerInitializer
 * Author:   胡烨
 * Date:     2018/10/26 16:42
 * Description:
 * History:
 *
 * @Version: 1.0
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // websocket 基于http协议，所以要有http编解码器
        pipeline.addLast(new HttpServerCodec());
        // 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        // 几乎在netty中的编程，都会使用到此hanler
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        // 对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", "", true));  //websocket的handler部分定义的，它会自己处理握手等操作
        //pipeline.addLast(new IQEncoder());
        //pipeline.addLast(new StringDecoder());
        pipeline.addLast(new MyServerHandler());
        pipeline.addLast(new AuthHandler());
        pipeline.addLast(new StanzaHandler());
        pipeline.addLast(new NettyIQHandler());
        pipeline.addLast(new NettyPresentHandler());
        pipeline.addLast(new NettyMessageHandler());
        //pipeline.addLast(new TestMyServerHandler());
        //pipeline.addLast(new MyStringHandler());
       // pipeline.addLast(new InterceptorsHandler());
    }
}
