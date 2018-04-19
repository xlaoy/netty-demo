package com.xlaoy.netty.demo05;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;
import org.msgpack.type.Value;

import java.util.List;

/**
 * Created by Administrator on 2018/4/19 0019.
 */
public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.getBytes(msg.readerIndex(), bytes, 0, msg.readableBytes());
        MessagePack pack = new MessagePack();
        Value value = pack.read(bytes, Templates.TValue);
        out.add(value);
    }
}
