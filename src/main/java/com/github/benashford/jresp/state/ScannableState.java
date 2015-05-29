package com.github.benashford.jresp.state;

import com.github.benashford.jresp.protocol.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

abstract class ScannableState implements State {
    private ByteBuf innerBuffer = Unpooled.directBuffer(1024);

    @Override
    public boolean decode(ByteBuf in) {
        int available = in.readableBytes();
        for (int i = 0; i < available; i++) {
            byte b = in.readByte();
            if (endOfString(b)) {
                return true;
            } else {
                innerBuffer.writeByte(b);
            }
        }
        return false;
    }

    private boolean endOfString(byte secondByte) {
        int idx = innerBuffer.writerIndex();
        if (innerBuffer.writerIndex() == 0) {
            return false;
        }
        if ((innerBuffer.getByte(idx - 1) == Resp.CRLF[0]) &&
                (secondByte == Resp.CRLF[1])) {
            return true;
        } else {
            return false;
        }
    }

    protected String bufferAsString() {
        return innerBuffer.toString(0, innerBuffer.writerIndex() - 1, Charset.forName("UTF-8"));
    }
}
