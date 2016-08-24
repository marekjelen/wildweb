package cz.wildweb.server;

import cz.wildweb.api.HttpContext;
import cz.wildweb.api.HttpFilter;
import cz.wildweb.api.HttpHandler;
import cz.wildweb.api.HttpServer;
import cz.wildweb.api.annotations.Request;
import cz.wildweb.server.router.HttpRouter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class HttpServerImpl implements HttpServer {

    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;
    private final HttpRouter router = new HttpRouter();
    private final HttpContext context;
    private boolean started = false;
    private Channel channel;

    public HttpServerImpl() {
        LoggerFactory.getLogger(getClass()).info("Setting up HTTP server");

        HttpServer self = this;

        this.context = new HttpContext();

        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();

        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(this.bossGroup, this.workerGroup);
        this.bootstrap.channel(NioServerSocketChannel.class);
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new LoggingHandler(LogLevel.DEBUG));
                p.addLast(new HttpRequestDecoder());
                p.addLast(new HttpResponseEncoder());
                p.addLast(new HttpServerHandler(self, router));
            }
        });
    }

    @Override
    public HttpContext context() {
        return this.context;
    }

    @Override
    public void start(String address, int port) {
        if(this.started) return;
        LoggerFactory.getLogger(getClass()).info("Starting HTTP server");

        try {
            this.channel = this.bootstrap.bind(address, port).sync().channel();
            this.started = true;
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(getClass()).error("Problem starting HTTP server", e);
            e.printStackTrace();
        }
    }

    @Override
    public void register(HttpHandler handler) {
        Request request = handler.getClass().getAnnotation(Request.class);
        register(request.method(), request.url(), handler);
    }

    @Override
    public void register(String url, HttpHandler handler) {
        this.register("*", url, handler);
    }

    @Override
    public void register(String method, String url, HttpHandler handler) {
        this.router.register(method, url, handler);
    }

    @Override
    public void stop() {
        if(!this.started) return;
        LoggerFactory.getLogger(getClass()).info("Stopping HTTP server");
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
        try {
            this.channel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.started = false;
    }

    @Override
    public void before(String method, String url, HttpFilter filter) {
        this.router.before(method, url, filter);
    }

    @Override
    public void after(String method, String url, HttpFilter filter) {
        this.router.after(method, url, filter);
    }

}
