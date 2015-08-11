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

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionReadGroup extends Thread {
    private int serialNo = 0;
    private Map<Integer, Connection> connections = Collections.synchronizedMap(new HashMap<>());
    private Selector selector;

    ConnectionReadGroup() throws IOException {
        selector = Selector.open();
    }

    public void run() {
        // TODO - graceful shutdown
        while (true) {
            try {
                selector.select(10);
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    Integer id = (Integer)key.attachment();
                    Connection con = connections.get(id);
                    con.readTick();
                }
            } catch (IOException e) {
                // TODO - notify the clients
                throw new RuntimeException(e);
            }
        }
    }

    public void add(Connection c) throws ClosedChannelException {
        int id = serialNo++;
        SelectionKey key = c.channel.register(selector, SelectionKey.OP_READ, id);
        connections.put(id, c);
    }

    public void shutdownGracefully() {
        throw new AssertionError("Unimplemented");
    }
}
