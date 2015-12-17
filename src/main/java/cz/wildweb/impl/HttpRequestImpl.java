package cz.wildweb.impl;

import cz.wildweb.api.HttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;

import java.util.*;

public class HttpRequestImpl implements HttpRequest {

    private final DefaultHttpRequest request;
    private final StringBuilder buffer = new StringBuilder();

    private Map<String, String> attributes = new HashMap<>();
    private List<String> splat;

    public HttpRequestImpl(DefaultHttpRequest request) {
        this.request = request;
    }

    @Override
    public String method() {
        return this.request.method().name();
    }

    @Override
    public String uri() {
        return this.request.uri();
    }

    @Override
    public String header(String name) {
        return this.request.headers().get(name);
    }

    @Override
    public Set<String> headers() {
        return this.request.headers().names();
    }

    @Override
    public String attribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Set<String> attributes() {
        return this.attributes.keySet();
    }

    @Override
    public List<String> splat() {
        return this.splat;
    }

    @Override
    public String content() {
        return this.buffer.toString();
    }

    public void content(String content) {
        this.buffer.append(content);
    }

    public void attribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public void splat(String[] splat) {
        this.splat = Arrays.asList(splat);
    }

}
