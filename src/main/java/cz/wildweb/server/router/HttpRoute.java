package cz.wildweb.server.router;

import cz.wildweb.api.HttpHandler;
import cz.wildweb.api.HttpRequest;

import java.util.*;

public class HttpRoute {

    private Map<String, HttpHandler> handlers = new HashMap<>();
    private Map<String, HttpRoute> nodes = new HashMap<>();
    private Map<String, HttpRoute> variables = new HashMap<>();

    public HttpHandler handler(String method) {
        if(this.handlers.containsKey(method)) {
            return this.handlers.get(method);
        } else {
            return this.handlers.get("*");
        }
    }

    public void handler(String method, HttpHandler handler) {
        this.handlers.put(method, handler);
    }

    public HttpRoute node(String name) {
        Map<String, HttpRoute> target = this.nodes;

        if(name.startsWith(":")) {
            target = this.variables;
            name = name.substring(1);
        }

        if (!target.containsKey(name)) {
            target.put(name, new HttpRoute());
        }

        return target.get(name);
    }

    public HttpUrlHolder match(HttpUrlHolder url, HttpRequest request) {
        // Unless we can get deeper, look for handler
        if(url.finished()) {
            HttpHandler result = handler(request.method());
            url.handler(result);
            return url;
        }

        // Going deeper
        Optional<String> segment = url.current();
        if(!segment.isPresent()) return null;

        HttpRoute route = this.nodes.get(segment.get());
        if(route != null) {
            HttpUrlHolder result = route.match(url.next(), request);
            if (result != null) {
                return result;
            }
        }

        for(String key : this.variables.keySet()) {
            route = this.variables.get(key);
            HttpUrlHolder result = route.match(url.next(), request);
            if(result != null) {
                result.variable(key, segment.get());
                return result;
            }
        }

        if(this.nodes.containsKey("*")) {
            HttpUrlHolder result;

            result = this.nodes.get("*").match(url, request);
            if(result != null) {
                return result;
            }

            HttpUrlHolder next = url.next();

            if(next.finished()) {
                result = this.nodes.get("*").match(next, request);
            } else {
                result = this.match(next, request);
            }

            if(result != null) {
                result.splat(segment.get());
                return result;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "HttpRoute{" +
                "handlers=" + handlers +
                ", nodes=" + nodes +
                ", variables=" + variables +
                '}';
    }
}
