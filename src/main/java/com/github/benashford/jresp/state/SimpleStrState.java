package com.github.benashford.jresp.state;

import com.github.benashford.jresp.protocol.RespType;
import com.github.benashford.jresp.protocol.SimpleStr;

public class SimpleStrState extends ScannableState {
    @Override
    public RespType finish() {
        return new SimpleStr(bufferAsString());
    }
}
