package cz.wildweb.server.servlet;

import cz.wildweb.api.WebSocket;

import java.util.function.Consumer;

public class ServletWebsocket implements WebSocket {
    @Override
    public boolean valid() {
        return false;
    }

    @Override
    public void accept() {

    }

    @Override
    public boolean active() {
        return false;
    }

    @Override
    public void write(String message) {

    }

    @Override
    public void close() {

    }

    @Override
    public void opened(Runnable activated) {

    }

    @Override
    public void message(Consumer<String> message) {

    }

    @Override
    public void closed(Runnable closed) {

    }
}
