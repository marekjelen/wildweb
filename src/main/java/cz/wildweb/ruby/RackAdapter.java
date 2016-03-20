package cz.wildweb.ruby;

import cz.wildweb.api.HttpHandler;
import cz.wildweb.api.HttpRequest;
import cz.wildweb.api.HttpResponse;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RackAdapter implements HttpHandler {

    private final String address;
    private final int port;
    private final String prefix;
    private final List<String> specialHeaders = Arrays.asList("CONTENT_TYPE", "CONTENT_LENGTH");
    private final boolean secure;
    private final IRubyObject app;
    private final Ruby runtime;

    public RackAdapter(String address, int port, String prefix, boolean secure, Ruby runtime, Object app) {
        this.address = address;
        this.port = port;
        this.prefix = prefix;
        this.secure = secure;
        this.runtime = runtime;
        this.app = (IRubyObject) app;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, Object> hash = new HashMap<>();

        hash.put("REQUEST_METHOD", request.method());
        hash.put("SCRIPT_NAME", this.prefix);
        hash.put("SERVER_NAME", this.address);
        hash.put("SERVER_PORT", String.valueOf(this.port));

        String[] uri = request.uri().split("\\?");
        String path = uri[0];
        String qr = uri.length == 1 ? "" : uri[1];

        hash.put("PATH_INFO", path);
        hash.put("QUERY_STRING", qr);

        request.headers().forEach(header -> {
            String name = header.toUpperCase();
            name = name.replace('-', '_');
            if(!this.specialHeaders.contains(header)) {
                name = "HTTP_" + name;
            }
            hash.put(name, request.header(header));
        });

        hash.put("rack.version", new int[] { 1,3 });
        hash.put("rack.url_scheme", this.secure ? "http" : "https");
        hash.put("rack.input", request.content());
        hash.put("rack.errors", null);
        hash.put("rack.multithread", true);
        hash.put("rack.multiprocess", false);
        hash.put("rack.run_once", false);
        hash.put("rack.hijack?", false);
        hash.put("rack.hijack", null);
        hash.put("rack.hijack_io", null);

        hash.put("wildweb.response", response);

        IRubyObject env = JavaUtil.convertJavaToRuby(this.runtime, hash);
        this.app.callMethod(runtime.getCurrentContext(), "call", env);
    }

}
