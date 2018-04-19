package com.xlaoy.netty.demo05;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/18 0018.
 */
public class TimeClient {

    public static void main(String[] args) throws Exception {
        EventLoopGroup loopGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("#".getBytes())));
                    socketChannel.pipeline().addLast(new MsgPackDecoder());
                    socketChannel.pipeline().addLast(new MsgPackEncoder());
                    socketChannel.pipeline().addLast(new TimeClientHandler());
                }
            });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            future.channel().closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    public static class TimeClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for(int i = 1; i < 51; i++) {
                ctx.write(new User(i, "xlaoy_" + i));
            }
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
