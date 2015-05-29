package com.github.benashford.jresp.state;

import com.github.benashford.jresp.protocol.Err;
import com.github.benashford.jresp.protocol.RespType;

public class ErrState extends ScannableState {
    @Override
    public RespType finish() {
        return new Err(bufferAsString());
    }
}
