package cz.wildweb.server.router;

import cz.wildweb.api.HttpHandler;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class HttpUrlHolder {

    private final String[] segments;
    private String[] variables;
    private String[] values;
    private String[] splat;
    private HttpHandler handler;

    public HttpUrlHolder(String url) {
        this.variables = new String[0];
        this.values = new String[0];
        this.splat = new String[0];

        if(url.startsWith("/")) url = url.substring(1);

        if(Objects.equals(url, "")) {
            this.segments = new String[0];
            return;
        }

        this.segments = url.split("/");
    }

    public HttpUrlHolder(String[] segments, String[] variables, String[] values, String[] splat) {
        this.segments = segments;
        this.variables = variables;
        this.values = values;
        this.splat = splat;
    }

    public boolean finished() {
        return this.segments.length == 0;
    }

    public Optional<String> current() {
        if(this.finished()) return Optional.empty();
        return Optional.of(this.segments[0]);
    }

    public HttpUrlHolder next() {
        if(this.finished()) {
            return new HttpUrlHolder(new String[0], this.variables, this.values, this.splat);
        }

        String[] segments = new String[this.segments.length - 1];
        System.arraycopy(this.segments, 1, segments, 0, this.segments.length - 1);
        return new HttpUrlHolder(segments, this.variables, this.values, this.splat);
    }

    public void variable(String name, String value) {
        this.variables = expand(this.variables);
        this.values = expand(this.values);
        this.variables[this.variables.length - 1] = name;
        this.values[this.values.length - 1] = value;
    }

    public void splat(String splat) {
        this.splat = expand(this.splat, 1);
        this.splat[0] = splat;
    }

    private String[] expand(String[] array) {
        return expand(array, 0);
    }

    private String[] expand(String[] array, int from) {
        String[] target = new String[array.length + 1];
        System.arraycopy(array, 0, target, from, array.length);
        return target;
    }

    public void handler(HttpHandler handler) {
        this.handler = handler;
    }

    public HttpHandler handler() {
        return handler;
    }

    public String[] variables() {
        return variables;
    }

    public String[] values() {
        return values;
    }

    public String[] splat() {
        return splat;
    }

    @Override
    public String toString() {
        return "HttpUrlHolder{" +
                "segments=" + Arrays.toString(segments) +
                ", variables=" + Arrays.toString(variables) +
                ", values=" + Arrays.toString(values) +
                ", splat=" + Arrays.toString(splat) +
                ", handler=" + handler +
                '}';
    }
}
