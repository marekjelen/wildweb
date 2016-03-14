package cz.wildweb.client;

import cz.wildweb.api.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.function.Consumer;

public class HttpClientImpl extends SimpleChannelInboundHandler<HttpObject> implements HttpClient {

    private final NioEventLoopGroup group;
    private final Bootstrap bootstrap;
    private Channel channel;
    private String host = "localhost";
    private int port = 80;
    private DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    private Consumer<HttpClientResponse> callback;
    private StringBuilder buffer = new StringBuilder();
    private HttpResponse response;
    private HashMap<String, String> headers;

    public HttpClientImpl() {
        this.request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        this.request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();

        final HttpClientImpl client = this;
        this.bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new LoggingHandler(LogLevel.INFO));
                p.addLast(new HttpClientCodec());
                p.addLast(new HttpContentDecompressor());
                p.addLast(client);
            }
        });
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, HttpObject message) {
        if (message instanceof HttpResponse) {
            this.response = (HttpResponse) message;
            this.headers = new HashMap<>();
            this.response.headers().forEach(entry -> {
                this.headers.put(entry.getKey(), entry.getValue());
            });
        }
        if (message instanceof HttpContent) {
            HttpContent content = (HttpContent) message;
            this.buffer.append(content.content().toString(CharsetUtil.UTF_8));
        }
        if (message instanceof LastHttpContent) {
            this.callback.accept(new HttpClientResponseImpl(this.response.status().code(), this.headers, this.buffer.toString()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public HttpClient secure() {
        this.port = 443;
        return this;
    }

    @Override
    public HttpClient open() {
        return open(this.host, this.port);
    }

    @Override
    public HttpClient open(String host) {
        return open(host, this.port);
    }

    @Override
    public HttpClient open(String host, int port) {
        this.host = host;
        this.port = port;
        this.request.headers().set(HttpHeaderNames.HOST, this.host + ":" + this.port);
        try {
            this.channel = this.bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public HttpClient method(String method) {
        this.request.setMethod(HttpMethod.valueOf(method));
        return this;
    }

    @Override
    public HttpClient uri(String uri) {
        this.request.setUri(uri);
        return this;
    }

    @Override
    public HttpClient header(String name, String value) {
        this.request.headers().set(name, value);
        return this;
    }

    @Override
    public HttpClient request(Consumer<HttpClientResponse> callback) {
        this.callback = callback;
        this.channel.writeAndFlush(request);
        return this;
    }

}
