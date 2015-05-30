package com.github.benashford.jresp;

import com.github.benashford.jresp.protocol.Ary;
import com.github.benashford.jresp.protocol.BulkStr;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ClientTest {
    @Test
    public void testClient() throws Exception {
        Client client = new Client("localhost", 6379);
        client.start();

        Connection con = client.getSharedConnection();
        con.write(Arrays.asList(new Ary(Arrays.asList(new BulkStr("PING")))));

        // TODO : get rid of this.
        Thread.sleep(5000);

        client.stop();
    }
}