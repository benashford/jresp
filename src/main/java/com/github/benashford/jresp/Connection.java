package com.github.benashford.jresp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * An individual connection
 */
public class Connection {
    private String hostname;
    private int port;
    private EventLoopGroup workers;

    private Channel channel;

    Connection(String hostname, int port, EventLoopGroup workers) {
        this.hostname = hostname;
        this.port = port;
        this.workers = workers;
    }

    void start() {
        Bootstrap b = new Bootstrap();
        b.group(workers).channel(NioSocketChannel.class);

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
}
