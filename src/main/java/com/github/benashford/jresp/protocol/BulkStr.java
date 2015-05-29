package com.github.benashford.jresp.protocol;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class BulkStr implements RespType {
    private byte[] payload;

    public BulkStr(String s) {
        try {
            payload = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void writeBytes(ByteBuf out) {
        out.writeChar('$');
        if (payload == null) {
            out.writeBytes(Resp.longToByteArray(-1));
        } else {
            out.writeBytes(Resp.longToByteArray(payload.length));
            out.writeBytes(payload);
        }
        out.writeBytes(Resp.CRLF);
    }
}
