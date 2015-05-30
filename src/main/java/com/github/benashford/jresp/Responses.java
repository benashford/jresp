package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.RespType;

public interface Responses {
    void responseReceived(RespType response);
}
