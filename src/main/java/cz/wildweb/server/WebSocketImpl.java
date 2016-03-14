package cz.wildweb.server;

import cz.wildweb.api.WebSocket;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class WebSocketImpl implements WebSocket {

    private final ChannelHandlerContext context;
    private final HttpRequestImpl request;

    private WebSocketServerHandshaker handshaker;
    private Consumer<String> onMessage;
    private Runnable onClosed;
    private boolean active;
    private Runnable opened;

    public WebSocketImpl(ChannelHandlerContext context, HttpRequestImpl request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public boolean valid() {
        return "Upgrade".equals(this.request.header("Connection")) && "websocket".equals(this.request.header("Upgrade"));
    }

    @Override
    public void accept() {
        String path = "ws://" + this.request.header("Host") + this.request.uri();
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(path, null, true);
        this.handshaker = wsFactory.newHandshaker(this.request.request());
        if (this.handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(this.context.channel());
        } else {
            this.handshaker.handshake(this.context.channel(), this.request.request()).addListener(future -> {
                LoggerFactory.getLogger(getClass()).debug("Websocket opened");
                this.active = true;
                if(this.opened != null) {
                    this.opened.run();
                }
            });
        }
    }

    @Override
    public boolean active() {
        return this.active;
    }

    @Override
    public void write(String message) {
        this.context.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(message.getBytes())));
    }

    @Override
    public void close() {
        this.handshaker.close(this.context.channel(), new CloseWebSocketFrame());
    }

    @Override
    public void opened(Runnable opened) {
        this.opened = opened;
    }

    @Override
    public void message(Consumer<String> message) {
        this.onMessage = message;
    }

    @Override
    public void closed(Runnable closed) {
        this.onClosed = closed;
    }

    public void onClosed(CloseWebSocketFrame frame) {
        LoggerFactory.getLogger(getClass()).debug("Websocket closed");
        if(frame != null) {
            this.handshaker.close(this.context.channel(), frame.retain());
        }
        if(this.onClosed != null) {
            this.onClosed.run();
        }
    }

    public void onPinged(PingWebSocketFrame frame) {
        LoggerFactory.getLogger(getClass()).debug("Websocket pinged");
        this.context.channel().write(new PongWebSocketFrame((frame.content().retain())));
    }

    public void onMessaged(Object msg) {
        if(msg  instanceof TextWebSocketFrame) {
            LoggerFactory.getLogger(getClass()).debug("Message: {}", ((TextWebSocketFrame) msg).text());
            if(this.onMessage != null) {
                this.onMessage.accept(((TextWebSocketFrame) msg).text());
            }
        }
    }

}
