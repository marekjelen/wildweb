package cz.wildweb.api;

import java.util.Set;

public interface HttpRequest {

    public String method();
    public String uri();

    public String header(String name);
    public Set<String> headers();

    public String attribute(String name);
    public Set<String> attributes();

    public String content();

}
