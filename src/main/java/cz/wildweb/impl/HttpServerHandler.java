package cz.wildweb.impl;

import cz.wildweb.api.HttpHandler;
import cz.wildweb.impl.router.HttpRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;

import java.io.File;

public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

    private final HttpRouter router;

    private ChannelHandlerContext context;

    private HttpRequestImpl request;
    private HttpResponseImpl response;

    public HttpServerHandler(HttpRouter router) {
        this.router = router;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(this.request.websocketHandler() != null && this.request.websocketHandler().active()) {
            this.request.websocketHandler().onClosed(null);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof WebSocketFrame) {
            if(msg instanceof CloseWebSocketFrame) {
                this.request.websocketHandler().onClosed((CloseWebSocketFrame) msg);
            }
            if(msg instanceof PingWebSocketFrame) {
                this.request.websocketHandler().onPinged((PingWebSocketFrame) msg);
            }
            if(msg instanceof TextWebSocketFrame) {
                this.request.websocketHandler().onMessaged(msg);
            }
            return;
        }

        if(msg instanceof DefaultHttpRequest) {
            this.request = new HttpRequestImpl((DefaultHttpRequest) msg, ctx);
            this.response = new HttpResponseImpl(ctx, this.request);
            LoggerFactory.getLogger(getClass()).debug("{} {}", this.request.method(), this.request.uri());
        }

        if(msg instanceof HttpContent) {
            this.request.content(((HttpContent) msg).content().toString(CharsetUtil.UTF_8));
        }

        if(msg instanceof LastHttpContent) {
            if(this.request.method().equals("GET")) {
                File path = new File("public", this.request.uri());
                if(path.exists() && path.isFile()) {
                    LoggerFactory.getLogger(getClass()).debug("Serving static file: {}", path.getAbsolutePath());
                    this.response.close(path);
                    return;
                }
            }
            route();
        }
    }

    private void route() throws Exception {
        HttpHandler handler = this.router.match(this.request);

        if(handler != null) {
            handler.handle(this.request, this.response);
        } else {
            this.response.status(404);
            this.response.close("Not found");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.write(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        LoggerFactory.getLogger(getClass()).error("HTTP server uncaught error", cause);
    }
}
