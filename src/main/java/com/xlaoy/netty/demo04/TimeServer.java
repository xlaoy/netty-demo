package com.xlaoy.netty.demo04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

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
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("#".getBytes())));
                    socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new StringEncoder());
                    socketChannel.pipeline().addLast(new TimeServerHandler());
                }
            });
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            System.out.println("服务器启动");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static class TimeServerHandler extends ChannelInboundHandlerAdapter {

        private Integer count = 0;

        @Override
        public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            /*ByteBuf byteBuf = (ByteBuf) o;
            byte[] requests = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(requests);
            String content = new String(requests, "UTF-8").substring(0, requests.length - "#".length());*/
            String msg = (String)o;
            System.out.println("body = " + msg + ", count = " + ++count);
            String respStr;
            if("get_time".equalsIgnoreCase(msg)) {
                respStr = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "\n";
            } else {
                respStr = "error\n";
            }
            ByteBuf response = Unpooled.copiedBuffer(respStr.getBytes());
            channelHandlerContext.writeAndFlush(response);
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
