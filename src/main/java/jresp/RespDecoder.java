/*
 * Copyright 2015 Ben Ashford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jresp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import jresp.state.*;

import java.util.List;

public class RespDecoder extends ByteToMessageDecoder {

    private State state = null;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (true) {
            int availableBytes = in.readableBytes();
            if (availableBytes == 0) {
                //
                // We need more bytes
                //
                break;
            }

            if (state == null) {
                //
                // There is no current state, so read the next byte
                //
                char nextChar = (char) in.readByte();
                state = nextState(nextChar);
            }
            if (state.decode(in)) {
                out.add(state.finish());
                state = null;
            } else {
                //
                // We need more bytes
                //
                break;
            }
        }
    }

    public static State nextState(char token) {
        switch (token) {
            case '+':
                return new SimpleStrState();
            case '-':
                return new ErrState();
            case ':':
                return new IntState();
            case '$':
                return new BulkStrState();
            case '*':
                return new AryState();
            default:
                throw new IllegalStateException(String.format("Unknown token %s", token));
        }
    }
}

