package cz.wildweb.impl;

import cz.wildweb.api.HttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;

import java.util.Map;
import java.util.Set;

public class HttpRequestImpl implements HttpRequest {

    private final DefaultHttpRequest request;
    private final StringBuilder buffer = new StringBuilder();

    private Map<String, String> attributes;

    public HttpRequestImpl(DefaultHttpRequest request) {
        this.request = request;
    }

    @Override
    public String method() {
        return this.request.getMethod().name();
    }

    @Override
    public String uri() {
        return this.request.getUri();
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
    public String content() {
        return this.buffer.toString();
    }

    public void attributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void content(String content) {
        this.buffer.append(content);
    }

}
