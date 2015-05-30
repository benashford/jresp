package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.Ary;
import com.github.benashford.jresp.protocol.BulkStr;
import com.github.benashford.jresp.protocol.RespType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ClientTest {
    private Client client;
    private Connection con;

    private CountDownLatch latch;

    private List<RespType> results = new ArrayList<>();

    @Before
    public void setup() {
        client = new Client("localhost", 6379);
        con = client.makeConnection(result -> {
            results.add(result);
            latch.countDown();
        });
    }

    @After
    public void teardown() {
        client.stop();
    }

    private RespType ping() {
        return new Ary(Arrays.asList(new BulkStr("PING")));
    }

    /**
     * Tests a single PING to a Redis server
     */
    @Test
    public void testPing() throws Exception {
        latch = new CountDownLatch(1);

        con.write(Arrays.asList(ping()));

        latch.await();
        RespType result = results.get(0);

        assertEquals("PONG", result.unwrap());
    }

    @Test
    public void testPings() throws Exception {
        latch = new CountDownLatch(2);

        con.write(Arrays.asList(ping(), ping()));

        latch.await();

        RespType result1 = results.get(0);
        RespType result2 = results.get(1);

        assertEquals("PONG", result1.unwrap());
        assertEquals("PONG", result2.unwrap());
    }

    @Test
    public void thousandPingTest() throws Exception {
        latch = new CountDownLatch(1000);

        con.write(IntStream.range(0, 1000).mapToObj(x -> ping()).collect(Collectors.toList()));

        latch.await();

        results.forEach(result -> assertEquals("PONG", result.unwrap()));
    }
}