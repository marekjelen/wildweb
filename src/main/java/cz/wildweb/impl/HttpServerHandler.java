package cz.wildweb.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;

public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

    private final HttpRouter router;

    private HttpRequestImpl request;
    private HttpResponseImpl response;

    public HttpServerHandler(HttpRouter router) {
        this.router = router;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof DefaultHttpRequest) {
            this.request = new HttpRequestImpl((DefaultHttpRequest) msg);
            this.response = new HttpResponseImpl(ctx);
            LoggerFactory.getLogger(getClass()).info("Request: {} to {}", this.request.method(), this.request.uri());
        }
        if(msg instanceof HttpContent) {
            this.request.content(((HttpContent) msg).content().toString(CharsetUtil.UTF_8));
        }
        if(msg instanceof LastHttpContent) {
            HttpRouterMatch match = this.router.match(this.request.method(), this.request.uri());
            if(match != null && match.handler() != null) {
                this.request.attributes(match.parameters());
                match.handler().handle(this.request, this.response);
            } else {
                this.response.status(404);
                this.response.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.write(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        LoggerFactory.getLogger(getClass()).error("HTTP server uncaught error", cause);
    }
}
