package com.github.benashford.jresp.protocol;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class Err implements RespType {
    private String payload;

    @Override
    public void writeBytes(ByteBuf out) {
        try {
            out.writeChar('-');
            out.writeBytes(payload.getBytes("UTF-8"));
            out.writeBytes(Resp.CRLF);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
