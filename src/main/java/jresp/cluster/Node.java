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

public class Node {
    private final String hostname;
    private final int port;

    public Node(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        return hostname.equals(node.hostname);

    }

    @Override
    public int hashCode() {
        int result = hostname.hashCode();
        result = 31 * result + port;
        return result;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}
