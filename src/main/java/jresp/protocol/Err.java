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

package jresp.protocol;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class Err implements RespType {
    private String payload;

    public Err(String payload) {
        this.payload = payload;
    }

    @Override
    public void writeBytes(ByteBuf out) {
        try {
            out.writeByte('-');
            out.writeBytes(payload.getBytes("UTF-8"));
            out.writeBytes(Resp.CRLF);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object unwrap() {
        return payload;
    }
}
