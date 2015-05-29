package com.github.benashford.jresp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * The owner of one-or-more connections.
 */
public class Client {
    private final String hostname;

    private final int port;

    private final EventLoopGroup workers;

    // TODO: also need non-shared connections
    private Connection sharedConnection;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        workers = new NioEventLoopGroup();
    }

    public void start() {
        sharedConnection = new Connection(hostname, port, workers);
        sharedConnection.start();
    }

    public Connection getSharedConnection() {
        return sharedConnection;
    }

    public void stop() {
        // TODO: shut-down individual connections
        // sharedConnection.stop();
        workers.shutdownGracefully();
    }
}
