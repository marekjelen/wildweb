package cz.wildweb.api;

import java.util.List;
import java.util.Set;

public interface HttpRequest {

    String method();
    String uri();

    String header(String name);
    Set<String> headers();

    String attribute(String name);
    Set<String> attributes();

    List<String> splat();

    String content();

}
