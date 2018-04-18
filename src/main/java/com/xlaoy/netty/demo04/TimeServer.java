package com.xlaoy.netty.demo04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2018/4/17 0017.
 */
public class TimeServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.childHandler(new ChildChannelHandler());
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            System.out.println("服务器启动");
            future.channel().closeFuture().sync();
            System.out.println("aaa");
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static class TimeServerHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            ByteBuf byteBuf = (ByteBuf) o;
            byte[] requests = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(requests);
            String content = new String(requests, "UTF-8");
            if("get time".equalsIgnoreCase(content)) {
                String time = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
                ByteBuf response = Unpooled.copiedBuffer(time.getBytes());
                channelHandlerContext.write(response);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
