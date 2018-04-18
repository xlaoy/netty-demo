package com.xlaoy.netty.demo03;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/4/16 0016.
 */
public class TimeServer {

    static CountDownLatch countDownLatch;

    static AsynchronousServerSocketChannel serverSocketChannel;

    public static void main(String[] args) throws Exception {
        TimeServer timeServer = new TimeServer();
        countDownLatch = new CountDownLatch(1);
        serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8000));
        serverSocketChannel.accept(timeServer, new CompletionHandler<AsynchronousSocketChannel, TimeServer>() {

            @Override
            public void completed(AsynchronousSocketChannel result, TimeServer attachment) {
                attachment.serverSocketChannel.accept(attachment, this);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                result.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {

                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {

                    }
                });
            }

            @Override
            public void failed(Throwable exc, TimeServer attachment) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
