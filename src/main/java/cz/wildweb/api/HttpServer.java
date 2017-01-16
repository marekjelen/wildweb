package cz.wildweb.api;

import cz.wildweb.api.annotations.Request;
import cz.wildweb.server.router.HttpRouter;

public interface HttpServer {

    HttpContext context();

    HttpRouter router();

    void start(String address, int port);

    void stop();

    default void register(HttpHandler handler) {
        Request request = handler.getClass().getAnnotation(Request.class);
        register(request.method(), request.url(), handler);
    }

    default void register(String url, HttpHandler handler) {
        this.register("*", url, handler);
    }

    default void register(String method, String url, HttpHandler handler) {
        router().register(method, url, handler);
    }

    default void before(String method, String url, HttpFilter filter) {
        router().before(method, url, filter);
    }

    default void after(String method, String url, HttpFilter filter) {
        router().after(method, url, filter);
    }

}
