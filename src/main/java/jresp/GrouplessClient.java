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
package jresp;

import java.io.IOException;

public class GrouplessClient {
    private final String hostname;

    private final int port;

    private String password;
    private Integer db;

    private ConnectionGroup group;

    public GrouplessClient(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    public Connection makeConnection() throws IOException {
        Connection con = new Connection(hostname, port, group);
        con.setPassword(password);
        con.setDb(db);
        return con;
    }

    public ConnectionGroup getGroup() {
        return group;
    }

    public void setGroup(ConnectionGroup group) {
        this.group = group;
    }

    public void shutdown() throws IOException {
        // no-op, subclasses may override
    }
}
