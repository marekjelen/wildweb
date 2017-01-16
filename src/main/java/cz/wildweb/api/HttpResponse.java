package cz.wildweb.api;

import cz.wildweb.server.templates.GenericTemplate;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface HttpResponse {

    HttpRequest request();

    Map<String, Object> variables();

    void status(int status);

    void header(String name, String value);

    void commit();

    boolean committed();

    void write(Object content);

    void write(byte[] buffer, int i, int len);

    void close();

    default void close(File file) {
        if(!file.exists()) {
            status(404);
            close();
            return;
        }

        try {
            FileInputStream stream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024 * 256];
            int len;
            try {
                while((len = stream.read(buffer)) != 0) {
                   write(buffer, 0, len);
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error("Problem reading from file stream", e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    LoggerFactory.getLogger(getClass()).error("Problem closing file stream", e);
                }
            }
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(getClass()).error("Problem opening file stream", e);
        }

        this.close();
    }

    default void close(Object content) {
        if(request().websocket().active()) return;

        String data = stringize(content);
        if(!this.committed()) {
            this.header("Content-Length", String.valueOf(data.length()));
        }
        this.write(data);
        this.close();
    }

    default void put(String name, Object value) {
        variables().put(name, value);
    }

    @SuppressWarnings("unchecked")
    default void render(String file) {
        if(request().websocket().active()) return;

        LoggerFactory.getLogger(getClass()).debug("Rendering template as response");

        int last = file.lastIndexOf(".");
        String ext = file.substring(last + 1);
        try {
            Class<GenericTemplate> clazz = (Class<GenericTemplate>) Class.forName("cz.wildweb.server.templates." + ext);
            GenericTemplate template = clazz.newInstance();
            this.close(template.render(variables(), file));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            status(500);
            close();
        }

    }

    default String stringize(Object content) {
        if(content == null) return "";
        if(content instanceof String) {
            return (String) content;
        } else {
            return content.toString();
        }
    }

}
