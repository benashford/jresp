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

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

class OutgoingBuffer {
    private static final int MAX_MERGED_BUFFER_SIZE = 1460;

    private final Deque<ByteBuffer> buffer = new ArrayDeque<>();

    private ByteBuffer current;

    /**
     * All ByteBuffers must be in write mode at this point, they'll get flipped by this buffer
     */
    synchronized void addAll(Collection<ByteBuffer> col) {
        if (col.isEmpty()) {
            throw new IllegalArgumentException("Empty col");
        }

        Consumer<ByteBuffer> addToCurrent = next -> {
            next.flip();
            int nextSize = next.remaining();
            while (nextSize > 0) {
                int currentRemaining = current.remaining();
                int allowableSize = Math.min(currentRemaining, nextSize);
                if (allowableSize == 0) {
                    current.flip();

                    buffer.add(current);

                    current = ByteBuffer.allocate(MAX_MERGED_BUFFER_SIZE);
                } else if (nextSize <= allowableSize) {
                    current.put(next);
                } else {
                    byte[] ary = new byte[allowableSize];
                    next.get(ary);
                    current.put(ary);
                }
                nextSize = next.remaining();
            }
        };

        for (ByteBuffer next : col) {
            if (current == null) {
                if (next.capacity() == MAX_MERGED_BUFFER_SIZE) {
                    current = next;
                } else {
                    current = ByteBuffer.allocate(MAX_MERGED_BUFFER_SIZE);
                    addToCurrent.accept(next);
                }
            } else {
                addToCurrent.accept(next);
            }
        }

        if (buffer.isEmpty()) {
            current.flip();
            buffer.add(current);
            current = null;
        }
    }

    public synchronized void addFirst(ByteBuffer bb) {
        buffer.addFirst(bb);
    }

    public synchronized ByteBuffer pop() {
        if (buffer.isEmpty()) {
            if (current == null) {
                return null;
            } else {
                ByteBuffer next = current;
                current = null;
                next.flip();
                return next;
            }
        } else {
            return buffer.pop();
        }
    }
}
