package cz.wildweb.server;

import cz.wildweb.api.HttpRequest;
import cz.wildweb.api.WebSocket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.util.CharsetUtil;

import java.util.*;

public class HttpRequestImpl implements HttpRequest {

    private final DefaultHttpRequest request;
    private final ChannelHandlerContext context;

    private ByteBuf buffer;

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
        return this.buffer.toString(CharsetUtil.UTF_8);
    }

    @Override
    public byte[] contentBytes() {
        int len = this.buffer.readableBytes();
        byte[] tmp = new byte[len];
        this.buffer.getBytes(0, tmp);
        return tmp;
    }

    public void content(ByteBuf content) {
        this.buffer = content;
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
