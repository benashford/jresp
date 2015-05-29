package com.github.benashford.jresp.state;

import com.github.benashford.jresp.RespDecoder;
import com.github.benashford.jresp.protocol.Ary;
import com.github.benashford.jresp.protocol.RespType;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class AryState implements State {
    private IntState intState = new IntState();
    private Integer aryLength = null;
    private List<RespType> ary = null;
    private State nextState = null;

    @Override
    public boolean decode(ByteBuf in) {
        if (aryLength == null) {
            if (intState.decode(in)) {
                aryLength = (int)intState.finishInt();
                ary = new ArrayList<>(aryLength);
            }
        }
        if (in.readableBytes() == 0) {
            return false;
        }
        if (nextState == null) {
            nextState = RespDecoder.nextState(in.readChar());
        }
        if (nextState.decode(in)) {
            ary.add(nextState.finish());
            nextState = null;
            return ary.size() == aryLength;
        } else {
            return false;
        }
    }

    @Override
    public RespType finish() {
        return new Ary(ary);
    }
}
