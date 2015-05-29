package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.RespType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RespEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        RespType message = (RespType)msg;
        message.writeBytes(out);
    }
}
