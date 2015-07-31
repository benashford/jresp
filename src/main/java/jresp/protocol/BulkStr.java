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

public class BulkStr implements RespType {
    private byte[] payload;

    public BulkStr(String s) {
        try {
            payload = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public BulkStr(byte[] s) {
        payload = s;
    }

    public BulkStr() {
        this.payload = null;
    }

    @Override
    public void writeBytes(ByteBuf out) {
        out.writeByte('$');
        if (payload == null) {
            out.writeBytes(Resp.longToByteArray(-1));
        } else {
            out.writeBytes(Resp.longToByteArray(payload.length));
            out.writeBytes(Resp.CRLF);
            out.writeBytes(payload);
        }
        out.writeBytes(Resp.CRLF);
    }

    public byte[] raw() {
        return payload;
    }

    @Override
    public Object unwrap() {
        if (payload == null) {
            return null;
        }
        try {
            return new String(payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
