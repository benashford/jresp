package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.RespType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RespHandler extends SimpleChannelInboundHandler<RespType> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RespType msg) throws Exception {
        System.out.printf("READ: %s\n", msg.unwrap());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("TODO: need actual error handling");
        cause.printStackTrace();
        ctx.close();
    }
}
