package com.xlaoy.netty.demo04;

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
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("#".getBytes())));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new StringEncoder());
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

        private Integer count = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            /*ByteBuf byteBuf = (ByteBuf)msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String response = new String(bytes, "UTF-8");*/
            System.out.println("response = " + msg + ", count = " + ++count);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            byte[] bytes = "get_time#".getBytes();
            for(int i = 0; i < 50; i++) {
                ByteBuf byteBuf = Unpooled.buffer(bytes.length);
                byteBuf.writeBytes(bytes);
                ctx.writeAndFlush(byteBuf);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
