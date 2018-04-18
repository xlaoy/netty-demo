package com.xlaoy.netty.demo02;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Created by Administrator on 2018/4/16 0016.
 */
public class TimeServer {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select(3000);
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            System.out.println(selectionKeySet.size() + "个已就绪");
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
        //新接入
        if(selectionKey.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        //可读
        if(selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int readBytes = socketChannel.read(byteBuffer);
            if(readBytes > 0) {
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                String content = new String(bytes, "UTF-8");
                if("get time".equalsIgnoreCase(content)) {
                    String time = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
                    byte[] writeBytes = time.getBytes();
                    ByteBuffer writeByteBuffer = ByteBuffer.allocate(writeBytes.length);
                    writeByteBuffer.put(writeBytes);
                    writeByteBuffer.flip();
                    socketChannel.write(writeByteBuffer);
                }
            } else {
                socketChannel.close();
                selectionKey.cancel();
            }
        }

    }
}
