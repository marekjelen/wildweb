package cz.wildweb.api;

public interface HttpServer {

    public void start(String address, int port);

    public void register(String url, HttpHandler handler);

    public void register(String method, String url, HttpHandler handler);

    public void unregister(HttpHandler handler);

    public void stop();

}
