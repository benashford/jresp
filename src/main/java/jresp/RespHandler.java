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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jresp.protocol.ClientErr;
import jresp.protocol.RespType;

public class RespHandler extends SimpleChannelInboundHandler<RespType> {
    private Responses responses;

    RespHandler(Responses responses) {
        this.responses = responses;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RespType msg) throws Exception {
        responses.responseReceived(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        responses.responseReceived(new ClientErr(cause));
        ctx.close();
    }
}
