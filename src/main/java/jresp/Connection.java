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

import jresp.protocol.ClientErr;
import jresp.protocol.RespType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An individual connection
 */
public class Connection {
    private static final int BYTE_BUFFER_DEFAULT_SIZE = 16;

    private String hostname;
    private int port;

    /**
     * The service threads
     */
    private ConnectionWriteGroup writeGroup;
    private ConnectionReadGroup readGroup;

    /**
     * The callback for incoming data
     */
    private Responses responses;

    /**
     * The socket channel
     */
    SocketChannel channel;

    /**
     * The buffer of out-going data
     */
    private Object bufferLock = new Object();
    private ByteBuffer[] buffers = new ByteBuffer[BYTE_BUFFER_DEFAULT_SIZE];
    private int buffersStart = 0;
    private int buffersEnd = 0;

    /**
     * Decoder
     */
    private RespDecoder decoder = new RespDecoder();

    /**
     * Constructor
     */
    Connection(String hostname,
               int port,
               ConnectionWriteGroup writeGroup,
               ConnectionReadGroup readGroup) {
        this.hostname = hostname;
        this.port = port;
        this.writeGroup = writeGroup;
        this.readGroup = readGroup;
    }

    void start(Responses responses) throws IOException {
        this.responses = responses;

        // TODO - make non-blocking
        this.channel = SocketChannel.open(new InetSocketAddress(hostname, port));
        this.channel.configureBlocking(false);
        writeGroup.add(this);
        readGroup.add(this);
    }

    void stop() {
        throw new AssertionError("Unimplemented");
    }

    private int bufferRemaining() {
        int right = buffersEnd;
        if (right < buffersStart) {
            right += buffers.length;
        }
        return buffers.length - (right - buffersStart);
    }

    private void addAllToBuffer(List<ByteBuffer> out) {
        for (ByteBuffer buf : out) {
            buffers[buffersEnd++] = buf;
            if (buffersEnd == buffers.length) {
                buffersEnd = 0;
            }
        }
    }

    private void resizeBuffer(int extraRequired) {
        int size = Math.max(buffers.length * 2, buffers.length + extraRequired + 1);

        ByteBuffer[] newBuffers = new ByteBuffer[size];
        int length;

        if (buffersEnd >= buffersStart) {
            length = buffersEnd - buffersStart;
        } else {
            length = buffers.length - buffersStart;
        }

        System.arraycopy(buffers, buffersStart, newBuffers, 0, length);

        if (buffersEnd < buffersStart) {
            System.arraycopy(buffers, 0, newBuffers, length, buffersEnd);
            length += buffersEnd;
        }

        buffers = newBuffers;
        buffersStart = 0;
        buffersEnd = length;
    }

    public void write(Collection<RespType> messages) {
        int bytes = 0;
        for (RespType message : messages) {
            List<ByteBuffer> out = new ArrayList<>();
            message.writeBytes(out);

            for (ByteBuffer o : out) {
                o.flip();
                bytes += o.remaining();
            }

            int outSize = out.size();

            synchronized (bufferLock) {
                int remaining = bufferRemaining();
                if (outSize >= remaining) {
                    resizeBuffer(outSize - remaining);
                }
                addAllToBuffer(out);
            }

            if (bytes >= 7936) {
                writeGroup.signal();
                bytes = 0;
            }
        }
        writeGroup.signal();
    }

    void writeTick() throws IOException {
        synchronized (bufferLock) {
            int end;
            if (buffersEnd >= buffersStart) {
                end = buffersEnd;
            } else {
                end = buffers.length;
            }
            int length = end - buffersStart;
            channel.write(buffers, buffersStart, length);

            int i = buffersStart;
            while (i < end && !buffers[i].hasRemaining()) {
                buffers[i++] = null;
                buffersStart++;
                if (buffersStart >= buffers.length) {
                    buffersStart = 0;
                }
            }

            if (buffersStart != buffersEnd) {
                writeGroup.signal();
            }
        }
    }

    void readTick() {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        try {
            channel.read(buf);
            buf.flip();
            List<RespType> out = new ArrayList<>();
            decoder.decode(buf, out);
            out.forEach(responses::responseReceived);
        } catch (IOException e) {
            responses.responseReceived(new ClientErr(e));
        }
    }

    void reportException(Exception e) {
        responses.responseReceived(new ClientErr(e));
    }
}
