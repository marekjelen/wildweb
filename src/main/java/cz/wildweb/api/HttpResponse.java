package cz.wildweb.api;

import java.io.File;

public interface HttpResponse {

    void status(int status);

    void header(String name, String value);

    void send();

    void write(Object content);

    void close();

    void close(File file);

    void close(Object content);

    void put(String name, Object value);

    void render(String file);

}
