package cz.wildweb.server.netty;

import cz.wildweb.api.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.*;

public class NettyRequest implements HttpRequestInternal {

    private final DefaultHttpRequest request;
    private final ChannelHandlerContext context;
    private final HttpServer server;

    private ByteBuf buffer = Unpooled.buffer();

    private Map<String, String> attributes = new HashMap<>();
    private Map<String, HttpFile> files = new HashMap<>();
    private List<String> splat;
    private NettyWebSocket websocket;
    private InterfaceHttpPostRequestDecoder decoder;

    public NettyRequest(DefaultHttpRequest request, HttpServer server, ChannelHandlerContext ctx) {
        this.server = server;
        this.context = ctx;
        this.request = request;
        this.decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(true), request);
    }

    @Override
    public HttpServer server() {
        return this.server;
    }

    @Override
    public HttpContext context() {
        return this.server.context();
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
            this.websocket = new NettyWebSocket(this.context, this);
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
    public HttpFile file(String name) {
        return this.files.get(name);
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
    public String content(String name) {
        return this.decoder.getBodyHttpData(name).toString();
    }

    @Override
    public byte[] contentBytes() {
        int len = this.buffer.readableBytes();
        byte[] tmp = new byte[len];
        this.buffer.getBytes(0, tmp);
        return tmp;
    }

    public void content(HttpContent content) {
        if(this.decoder.isMultipart() && !(content instanceof LastHttpContent)) {
            this.decoder.offer(content);
            while (this.decoder.hasNext()) {
                InterfaceHttpData part = this.decoder.next();
                if (part instanceof Attribute) {
                    try {
                        this.attributes.put(part.getName(), ((Attribute) part).getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (part instanceof FileUpload) {
                    this.files.put(part.getName(), new NettyFile((FileUpload) part));
                }
            }
        }
        this.buffer = this.buffer.writeBytes(content.content());
        if(content instanceof LastHttpContent
                && request.headers().get(HttpHeaderNames.CONTENT_TYPE) != null
                && request.headers().get(HttpHeaderNames.CONTENT_TYPE).equals("application/x-www-form-urlencoded")){
            String b = this.buffer.toString(CharsetUtil.UTF_8);
            QueryStringDecoder qs = new QueryStringDecoder(b, false);
            qs.parameters().keySet().stream().forEach(name -> {
                this.attribute(name, qs.parameters().get(name).get(0));
            });
        }
    }

    @Override
    public void attribute(String name, String value) {
        this.attributes.put(name, value);
    }

    @Override
    public void splat(String[] splat) {
        this.splat = Arrays.asList(splat);
    }

    public DefaultHttpRequest request() {
        return request;
    }

    public NettyWebSocket websocketHandler() {
        return websocket;
    }

    public void close() {
        try {
            this.decoder.destroy();
        } catch (IllegalStateException e) {

        }
    }
}