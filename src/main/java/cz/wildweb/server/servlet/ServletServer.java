package cz.wildweb.server.servlet;

import cz.wildweb.api.HttpContext;
import cz.wildweb.api.HttpServer;
import cz.wildweb.server.router.HttpRouter;

public class ServletServer implements HttpServer {

    private final HttpContext context;
    private final HttpRouter router;

    public ServletServer() {
        this.context = new HttpContext();
        this.router = new HttpRouter();
    }

    @Override
    public HttpContext context() {
        return this.context;
    }

    @Override
    public HttpRouter router() {
        return this.router;
    }


    @Override
    public void start(String address, int port) {
        // Not required when used in servlet container
    }

    @Override
    public void stop() {
        // Not required when used in servlet container
    }
}
