package cz.wildweb.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HttpRouterPattern {

    private final String method;
    private final String url;
    private final Pattern pattern;
    private final List<String> names = new ArrayList<>();

    public HttpRouterPattern(String method, String url, Pattern pattern) {
        this.method = method;
        this.url = url;
        this.pattern = pattern;
    }

    public String url() {
        return url;
    }

    public String method() {
        return method;
    }

    public Pattern pattern() {
        return pattern;
    }

    public List<String> names() {
        return names;
    }

    public void name(String name) {
        this.names.add(name);
    }

    @Override
    public String toString() {
        return "HttpRouterPattern{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", pattern=" + pattern +
                ", names=" + names +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpRouterPattern that = (HttpRouterPattern) o;

        if (!method.equals(that.method)) return false;
        if (!url.equals(that.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
