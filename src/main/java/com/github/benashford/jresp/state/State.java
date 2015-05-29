package com.github.benashford.jresp.state;

import com.github.benashford.jresp.protocol.RespType;
import io.netty.buffer.ByteBuf;

public interface State {
    boolean decode(ByteBuf in);
    RespType finish();
}
