package com.github.benashford.jresp.state;

import com.github.benashford.jresp.protocol.Int;
import com.github.benashford.jresp.protocol.RespType;

public class IntState extends ScannableState {
    @Override
    public RespType finish() {
        return new Int(finishInt());
    }

    long finishInt() {
        return Long.parseLong(bufferAsString());
    }
}
