package com.xlaoy.netty.demo02;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Created by Administrator on 2018/4/16 0016.
 */
public class TimeClient {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        boolean connect = socketChannel.connect(new InetSocketAddress("127.0.0.1", 8000));
        if(connect) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
        while (true) {
            selector.select(200);
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            for(SelectionKey selectionKey : selectionKeySet) {
                try {
                    handleKey(selectionKey, selector);
                } catch (Exception e) {
                    if(selectionKey != null) {
                        if(selectionKey.channel() != null) {
                            selectionKey.channel().close();
                        }
                        selectionKey.cancel();
                    }
                }
            }
        }
    }

    private static void handleKey(SelectionKey selectionKey, Selector selector) throws Exception {
        if(selectionKey == null) {
            return;
        }
        if(!selectionKey.isValid()) {
            return;
        }

        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

        if(selectionKey.isConnectable()) {
            if(socketChannel.finishConnect()) {
                socketChannel.register(selector, SelectionKey.OP_READ);
                doWrite(socketChannel);
            } else {
                System.exit(1);
            }
        }

        if(selectionKey.isReadable()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int readBytes = socketChannel.read(byteBuffer);
            if(readBytes > 0) {
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                String content = new String(bytes, "UTF-8");
                System.out.println(content);
            } else {
                socketChannel.close();
                selectionKey.cancel();
            }
        }
    }

    private static void doWrite(SocketChannel socketChannel) throws Exception {
        byte[] writeBytes = "get time".getBytes();
        ByteBuffer writeByteBuffer = ByteBuffer.allocate(writeBytes.length);
        writeByteBuffer.put(writeBytes);
        writeByteBuffer.flip();
        socketChannel.write(writeByteBuffer);
    }
}
