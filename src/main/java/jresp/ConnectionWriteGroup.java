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

import jresp.util.Signaller;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class ConnectionWriteGroup extends Thread {
    private static int threadId = 1;

    private int serialNo = 0;
    private Map<Integer, Connection> connections = Collections.synchronizedMap(new HashMap<>());
    private Signaller signaller = new Signaller();
    private Selector selector;

    private boolean shutdown;

    ConnectionWriteGroup() throws IOException {
        selector = Selector.open();

        setName(String.format("ConnectionWriteGroup-%d", threadId++));
        setDaemon(true);
    }

    public void run() {
        // TODO - shutdown gracefully
        while (!shutdown) {
            Set<Connection> cons = signaller.reset();
            while (!cons.isEmpty()) {
                try {
                    selector.select(10);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        Connection connection = connections.get(key.attachment());
                        if (cons.remove(connection)) {
                            try {
                                connection.writeTick();
                            } catch (IOException e) {
                                connection.reportException(e);
                            }
                        }
                    }
                } catch (IOException e) {
                    // TODO - notify the clients
                    throw new RuntimeException(e);
                }
            }
        }
    }

    void signal(Connection c) {
        signaller.signal(c);
    }

    public void add(Connection c) throws ClosedChannelException {
        int id = serialNo++;
        SelectionKey key = c.channel.register(selector, SelectionKey.OP_WRITE, id);
        connections.put(id, c);
    }

    public void shutdown() {
        shutdown = true;
    }
}
