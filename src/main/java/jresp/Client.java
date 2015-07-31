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

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * The owner of one-or-more connections.
 */
public class Client {
    private final String hostname;

    private final int port;

    private final EventLoopGroup workers;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        workers = new NioEventLoopGroup();
    }

    public Connection makeConnection(Responses responses) {
        Connection con = new Connection(hostname, port, workers);
        con.start(responses);
        return con;
    }

    public void stop() {
        workers.shutdownGracefully();
    }
}
