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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConnectionWriteGroup extends Thread {
    private Set<Connection> connections = Collections.synchronizedSet(new HashSet<>());
    private Signaller signaller = new Signaller();

    public void run() {
        // TODO - shutdown gracefully
        while (true) {
            signaller.reset();
            for (Connection connection : connections) {
                try {
                    connection.writeTick();
                } catch (IOException e) {
                    connection.reportException(e);
                }
            }
        }
    }

    void signal() {
        signaller.signal();
    }

    public void add(Connection c) {
        connections.add(c);
    }

    public void shutdownGracefully() {
        //throw new AssertionError("Unimplemented");
    }
}
