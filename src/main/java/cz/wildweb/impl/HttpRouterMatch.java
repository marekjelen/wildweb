package cz.wildweb.impl;

import cz.wildweb.api.HttpHandler;

import java.util.HashMap;
import java.util.Map;

public class HttpRouterMatch {

    private final HttpRouterPattern pattern;
    private final HttpHandler handler;
    private final Map<String, String> parameters = new HashMap<>();

    public HttpRouterMatch(HttpRouterPattern pattern, HttpHandler handler) {
        this.pattern = pattern;
        this.handler = handler;
    }

    public HttpRouterPattern pattern() {
        return pattern;
    }

    public HttpHandler handler() {
        return handler;
    }

    public Map<String, String> parameters() {
        return parameters;
    }

}
