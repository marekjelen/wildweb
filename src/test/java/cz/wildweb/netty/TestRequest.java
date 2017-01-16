package cz.wildweb.netty;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestRequest extends BaseTest {

    @Test
    public void connect() throws IOException {
        getServer().register("/", (request, response) -> {
            response.close("xyz");
        });

        startServer();

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", getPort()));

        socket.getOutputStream().write(("GET / HTTP/1.1\r\nHost: localhost:" + getPort() + "\r\n\r\n").getBytes());
        socket.getOutputStream().flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Assert.assertEquals("HTTP/1.1 200 OK", reader.readLine());
        Assert.assertEquals("Content-Length: 3", reader.readLine());
        Assert.assertEquals("", reader.readLine());
        Assert.assertEquals("xyz", reader.readLine());
    }

}
