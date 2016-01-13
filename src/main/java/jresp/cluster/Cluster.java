/*
 * Copyright 2016 Ben Ashford
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
package jresp.cluster;

import jresp.ConnectionGroup;
import jresp.GrouplessClient;
import jresp.ShutdownException;
import jresp.pool.Pool;

import java.io.IOException;
import java.util.*;

public class Cluster {
    private final ConnectionGroup group;

    private final Map<Node, Pool> knownConnections = new HashMap<>();

    public Cluster(String hostname, int port) throws IOException {
        this(new Node(hostname, port));
    }

    public Cluster(Node hostAndPort) throws IOException {
        this(Arrays.asList(hostAndPort));
    }

    public Cluster(Collection<Node> startNodes) throws IOException {
        group = new ConnectionGroup();
        group.start();

        for (Node node : startNodes) {
            GrouplessClient c = new GrouplessClient(node.getHostname(), node.getPort());
            c.setGroup(group);
            knownConnections.put(node, new Pool(c));
        }
    }

    public void shutdown() throws IOException {
        List<IOException> shutdownExceptions = null;
        for (Pool pool : knownConnections.values()) {
            try {
                pool.shutdown();
            } catch (IOException e) {
                if (shutdownExceptions == null) {
                    shutdownExceptions = new ArrayList<>();
                }
                shutdownExceptions.add(e);
            }
        }
        group.shutdown();

        if (shutdownExceptions != null) {
            throw new ShutdownException(shutdownExceptions);
        }
    }
}
