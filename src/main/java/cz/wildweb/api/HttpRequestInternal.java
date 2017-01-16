package cz.wildweb.api;

public interface HttpRequestInternal extends HttpRequest {

    void attribute(String name, String value);

    void splat(String[] splat);

}
