package cz.wildweb.api;

import java.io.File;

public interface HttpResponse {

    public void status(int status);

    public void header(String name, String value);

    public void send();

    public void write(Object content);

    public void close();

    public void close(File file);

    public void close(Object content);

    public void put(String name, Object value);

    public void render(String file);

}
