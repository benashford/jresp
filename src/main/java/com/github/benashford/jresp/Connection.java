package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.RespType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Collection;

/**
 * An individual connection
 */
public class Connection {
    private String hostname;
    private int port;
    private EventLoopGroup workers;

    private Channel channel;

    Connection(String hostname,
               int port,
               EventLoopGroup workers) {
        this.hostname = hostname;
        this.port = port;
        this.workers = workers;
    }

    void start(Responses responses) {
        Bootstrap b = new Bootstrap();
        b.group(workers).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new RespDecoder(), new RespEncoder(), new RespHandler(responses));
            }
        });

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

    void write(Collection<RespType> messages) {
        messages.stream().forEach(x -> channel.write(x));
        channel.flush();
    }
}
