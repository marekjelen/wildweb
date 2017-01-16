package cz.wildweb.server.servlet;

import cz.wildweb.api.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class ServletRequest implements HttpRequestInternal {

    private final HttpServletRequest request;
    private final ServletServer server;
    private final HttpContext context;

    private final HashSet<String> headerNames;
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, HttpFile> files = new HashMap<>();
    private List<String> splat = new LinkedList<>();
    private ServletWebsocket websocket = new ServletWebsocket();
    private byte[] content = new byte[0];

    public ServletRequest(HttpServletRequest request, ServletServer server, HttpContext context) {
        this.request = request;
        this.server = server;
        this.context = context;

        this.headerNames = new HashSet<>();
        Enumeration<String> en = this.request.getHeaderNames();
        while (en.hasMoreElements()) {
            this.headerNames.add(en.nextElement());
        }

    }

    @Override
    public HttpServer server() {
        return this.server;
    }

    @Override
    public HttpContext context() {
        return this.context;
    }

    @Override
    public String method() {
        return this.request.getMethod();
    }

    @Override
    public String uri() {
        return this.request.getRequestURI();
    }

    @Override
    public WebSocket websocket() {
        return this.websocket;
    }

    @Override
    public String header(String name) {
        return this.request.getHeader(name);
    }

    @Override
    public Set<String> headers() {
        return this.headerNames;
    }

    @Override
    public String attribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Set<String> attributes() {
        return this.attributes.keySet();
    }

    @Override
    public HttpFile file(String name) {
        return this.files.get(name);
    }

    @Override
    public List<String> splat() {
        return this.splat;
    }

    @Override
    public String content() {
        return new String(this.content);
    }

    @Override
    public String content(String name) {
        String[] value = this.request.getParameterValues(name);
        if(value == null) return null;
        return value[0];
    }

    @Override
    public byte[] contentBytes() {
        return this.content;
    }

    public void readRequest() throws IOException {
        if(!this.request.getInputStream().isReady()) return;

        byte[] buffer = new byte[1024 * 1024 * 256];
        int len;
        while((len = this.request.getInputStream().read(buffer)) != 0) {
            int size = this.content.length + len;
            byte[] tmp = new byte[size];
            System.arraycopy(this.content, 0, tmp, 0, this.content.length);
            System.arraycopy(buffer, 0, tmp, this.content.length, len);
            this.content = tmp;
        }
    }

    @Override
    public void attribute(String name, String value) {
        this.attributes.put(name, value);
    }

    @Override
    public void splat(String[] splat) {
        this.splat.addAll(Arrays.asList(splat));
    }
}
