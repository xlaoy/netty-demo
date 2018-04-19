package com.xlaoy.netty.demo05;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * Created by Administrator on 2018/4/19 0019.
 */
public class MsgPackEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        MessagePack pack = new MessagePack();
        byte[] bytes = pack.write(msg);
        out.writeBytes(bytes);
    }
}
