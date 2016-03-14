package cz.wildweb;

import cz.wildweb.api.HttpClient;
import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestServer extends BaseTest {

    @Before
    public void setup() {
        startServer();
    }

    @Test
    public void start() throws IOException {
//        Socket socket = new Socket();
//        socket.connect(new InetSocketAddress("localhost", getPort()));
//
//        socket.getOutputStream().write(("GET / HTTP/1.1\r\nHost: localhost:" + getPort() + "\r\n\r\n").getBytes());
//        socket.getOutputStream().flush();

//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        Assert.assertEquals(reader.readLine(), "HTTP/1.1 404 Not Found");
    }

}
