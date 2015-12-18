package cz.wildweb.api;

import java.util.function.Consumer;

public interface WebSocket {

    boolean valid();

    void accept();

    boolean active();

    void write(String message);

    void close();

    void opened(Runnable activated);

    void message(Consumer<String> message);

    void closed(Runnable closed);

}
