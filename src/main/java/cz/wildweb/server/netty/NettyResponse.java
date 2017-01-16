package cz.wildweb.server.netty;

import cz.wildweb.api.HttpRequest;
import cz.wildweb.api.HttpResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class NettyResponse implements HttpResponse {

    private final ChannelHandlerContext channelContext;
    private final DefaultHttpResponse response;
    private final Map<String, Object> variables = new HashMap<>();
    private final NettyRequest request;
    private boolean sent = false;

    public NettyResponse(ChannelHandlerContext channelContext, NettyRequest request) {
        this.request = request;
        this.channelContext = channelContext;
        this.response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    }

    @Override
    public HttpRequest request() {
        return this.request;
    }

    @Override
    public Map<String, Object> variables() {
        return this.variables;
    }

    @Override
    public void status(int status) {
        this.response.setStatus(HttpResponseStatus.valueOf(status));
    }

    @Override
    public void header(String name, String value) {
        this.response.headers().set(name, value);
    }

    @Override
    public void commit() {
        if(this.request.websocket().active()) return;
        if(this.sent) return;

        this.sent = true;
        LoggerFactory.getLogger(getClass()).debug("Sending response header");
        this.channelContext.writeAndFlush(this.response);
    }

    @Override
    public boolean committed() {
        return this.sent;
    }

    @Override
    public void write(Object content) {
        if(this.request.websocket().active()) return;

        this.commit();
        String data = stringize(content);
        LoggerFactory.getLogger(getClass()).debug("Sending response body {}", data);
        this.channelContext.write(new DefaultHttpContent(Unpooled.wrappedBuffer(data.getBytes())));
    }

    @Override
    public void write(byte[] buffer, int i, int len) {
        if(this.request.websocket().active()) return;
        this.commit();
        this.channelContext.write(new DefaultHttpContent(Unpooled.wrappedBuffer(buffer, i, len)));
    }

    @Override
    public void close() {
        if(this.request.websocket().active()) return;

        LoggerFactory.getLogger(getClass()).debug("Closing response");

        this.commit();

        this.channelContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener((future) -> {
            ChannelFuture f = (ChannelFuture) future;
            f.channel().close();
            this.request.close();
        });
    }

    @Override
    public void close(File file) {
        if(this.request.websocket().active()) return;

        LoggerFactory.getLogger(getClass()).debug("Sending file as response");

        if(!file.exists()) {
            status(404);
            close();
            return;
        }

        RandomAccessFile raf;
        long length;
        FileChannel channel;

        try {
            raf = new RandomAccessFile(file, "r");
            length = raf.length();
            channel = raf.getChannel();
        } catch (IOException e) {
            status(500);
            close();
            return;
        }

        header("Content-Length", String.valueOf(length));

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        header("Content-Type", mimeTypesMap.getContentType(file));

        commit();

        this.channelContext.writeAndFlush(new DefaultFileRegion(channel, 0, length));

        this.close();
    }

}
