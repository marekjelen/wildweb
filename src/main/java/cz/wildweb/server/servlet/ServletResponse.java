package cz.wildweb.server.servlet;

import cz.wildweb.api.HttpRequest;
import cz.wildweb.api.HttpResponse;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletResponse implements HttpResponse {

    private final HttpServletResponse response;
    private final Map<String, Object> variables = new HashMap<>();
    private final ServletRequest request;

    public ServletResponse(HttpServletResponse response, ServletRequest request) {
        this.response = response;
        this.request = request;
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
        this.response.setStatus(status);
    }

    @Override
    public void header(String name, String value) {
        if(this.committed()) return;
        if("Content-Length".equals(name)) {
            this.response.setContentLength(Integer.parseInt(value));
        } else {
            this.response.setHeader(name, value);
        }
    }

    @Override
    public void commit() {
        if(this.committed()) return;
        try {
            this.response.getOutputStream().flush();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Problem flushing servlet output stream", e);
        }
    }

    @Override
    public boolean committed() {
        return this.response.isCommitted();
    }

    @Override
    public void write(Object content) {
        this.commit();

        try {
            this.response.getOutputStream().write(stringize(content).getBytes());
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Problem sending content to servlet output stream", e);
        }
    }

    @Override
    public void write(byte[] buffer, int pos, int len) {
        this.commit();

        try {
            this.response.getOutputStream().write(buffer, pos, len);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Problem sending content to servlet output stream", e);
        }
    }

    @Override
    public void close() {
        this.commit();

        try {
            this.response.getOutputStream().close();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Problem closing servlet output stream", e);
        }
    }

}
