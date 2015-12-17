package cz.wildweb.api;

public interface HttpServer {

    void start(String address, int port);

    void register(String url, HttpHandler handler);

    void register(String method, String url, HttpHandler handler);

    void stop();

}
