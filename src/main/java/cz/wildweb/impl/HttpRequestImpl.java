package cz.wildweb.impl;

import cz.wildweb.api.HttpRequest;
import cz.wildweb.api.WebSocket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;

import java.util.*;

public class HttpRequestImpl implements HttpRequest {

    private final DefaultHttpRequest request;
    private final ChannelHandlerContext context;
    private final StringBuilder buffer = new StringBuilder();

    private Map<String, String> attributes = new HashMap<>();
    private List<String> splat;
    private WebSocketImpl websocket;

    public HttpRequestImpl(DefaultHttpRequest request, ChannelHandlerContext ctx) {
        this.context = ctx;
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
    public WebSocket websocket() {
        if(this.websocket == null) {
            this.websocket = new WebSocketImpl(this.context, this);
        }
        return this.websocket;
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

    public DefaultHttpRequest request() {
        return request;
    }

    public WebSocketImpl websocketHandler() {
        return websocket;
    }

}
