package cz.wildweb.api;

import java.util.List;
import java.util.Set;

public interface HttpRequest {

    HttpServer server();
    HttpContext context();

    String method();
    String uri();

    WebSocket websocket();

    String header(String name);
    Set<String> headers();

    String attribute(String name);
    Set<String> attributes();

    HttpFile file(String name);

    List<String> splat();

    String content();
    String content(String name);
    byte[] contentBytes();

}
