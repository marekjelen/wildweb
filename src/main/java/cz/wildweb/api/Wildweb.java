package cz.wildweb.api;

import cz.wildweb.server.netty.NettyServer;

public class Wildweb {

    protected Wildweb() {
    }

    private static HttpServer server = new NettyServer();

    public static HttpServer getServer() {
        return server;
    }

    public static void before(String method, String url, HttpFilter filter) {
        getServer().before(method, url, filter);
    }

    public static void after(String method, String url, HttpFilter filter) {
        getServer().after(method, url, filter);
    }

    public static void request(HttpHandler handler) {
        getServer().register(handler);
    }

    public static void request(String url, HttpHandler handler) {
        request("*", url, handler);
    }

    public static void request(String method, String url, HttpHandler handler) {
        getServer().register(method, url, handler);
    }

    public static void get(String url, HttpHandler handler) {
        getServer().register("GET", url, handler);
    }

    public static void post(String url, HttpHandler handler) {
        getServer().register("POST", url, handler);
    }

    public static void put(String url, HttpHandler handler) {
        getServer().register("PUT", url, handler);
    }

    public static void delete(String url, HttpHandler handler) {
        getServer().register("DELETE", url, handler);
    }

    public static void patch(String url, HttpHandler handler) {
        getServer().register("PATCH", url, handler);
    }

    public static void head(String url, HttpHandler handler) {
        getServer().register("HEAD", url, handler);
    }

    public static void trace(String url, HttpHandler handler) {
        getServer().register("TRACE", url, handler);
    }

    public static void connect(String url, HttpHandler handler) {
        getServer().register("CONNECT", url, handler);
    }

    public static void options(String url, HttpHandler handler) {
        getServer().register("OPTIONS", url, handler);
    }

    public static void startServer(int port) {
        startServer("0.0.0.0", port);
    }

    public static void startServer(String addr, int port) {
        getServer().start(addr, port);
    }

    public static void stopServer() {
        getServer().stop();
    }

}
