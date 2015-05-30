package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.Ary;
import com.github.benashford.jresp.protocol.BulkStr;
import com.github.benashford.jresp.protocol.RespType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class ClientTest {
    /**
     * Tests a single PING to a Redis server
     */
    @Test
    public void testPing() throws Exception {
        Client client = new Client("localhost", 6379);

        List<RespType> results = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        Connection con = client.makeConnection(result -> {
            results.add(result);
            latch.countDown();
        });

        con.write(Arrays.asList(new Ary(Arrays.asList(new BulkStr("PING")))));

        latch.await();
        RespType result = results.get(0);

        client.stop();

        assertEquals("PONG", result.unwrap());
    }
}