package cz.wildweb;

import cz.wildweb.api.HttpServer;
import cz.wildweb.server.HttpServerImpl;
import org.junit.After;
import org.junit.Before;

public class BaseTest {

    private int port = 9090;
    private HttpServer server;

    @Before
    public void before() {
        this.server = new HttpServerImpl();
    }

    @After
    public void after() {
        this.server.stop();
    }

    public int getPort() {
        return port;
    }

    public HttpServer getServer() {
        return server;
    }

    public void startServer() {
        this.server.start("localhost", this.port);
    }

}
