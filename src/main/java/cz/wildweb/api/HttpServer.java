package cz.wildweb.api;

public interface HttpServer {

    HttpContext context();

    void start(String address, int port);

    void register(HttpHandler handler);

    void register(String url, HttpHandler handler);

    void register(String method, String url, HttpHandler handler);

    void stop();

    void before(String method, String url, HttpFilter filter);

    void after(String method, String url, HttpFilter filter);

}
