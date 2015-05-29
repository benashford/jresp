package com.github.benashford.jresp.protocol;

import io.netty.buffer.ByteBuf;

public class Int implements RespType {
    private long payload;

    @Override
    public void writeBytes(ByteBuf out) {
        out.writeChar(':');
        out.writeBytes(Resp.longToByteArray(payload));
        out.writeBytes(Resp.CRLF);
    }
}
