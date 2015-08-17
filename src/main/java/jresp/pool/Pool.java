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

package jresp.pool;

import jresp.Client;

import java.io.IOException;

/**
 * A connection-pool for JRESP connections.  Since JRESP is asynchronous, most operations can be multiplexed onto one
 * single shared connection.  However there are some Redis commands that are exceptions to this rule.
 */
public class Pool {
    private Client client;

    private SharedConnection shared;

    public Pool(Client client) {
        this.client = client;
    }

    /**
     * The shared connection is for the majority of Redis commands that return one single response.  JRESP will
     * automatically pipeline such commands for efficiency.
     *
     * Do not use such a connection for any blocking, pub-sub, or any other command that doesn't return one single
     * response.
     */
    public synchronized SharedConnection getShared() throws IOException {
        if (shared == null) {
            shared = new SharedConnection(client.makeConnection());
        }

        return shared;
    }
}
