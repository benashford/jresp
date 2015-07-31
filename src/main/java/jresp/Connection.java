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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jresp.protocol.RespType;

import java.util.Collection;

/**
 * An individual connection
 */
public class Connection {
    private String hostname;
    private int port;
    private EventLoopGroup workers;

    private Channel channel;

    Connection(String hostname,
               int port,
               EventLoopGroup workers) {
        this.hostname = hostname;
        this.port = port;
        this.workers = workers;
    }

    void start(Responses responses) {
        Bootstrap b = new Bootstrap();
        b.group(workers).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new RespDecoder(), new RespEncoder(), new RespHandler(responses));
            }
        });

        try {
            channel = b.connect(hostname, port).sync().channel();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    void stop() {
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public void write(Collection<RespType> messages) {
        messages.stream().forEach(channel::write);
        channel.flush();
    }
}
