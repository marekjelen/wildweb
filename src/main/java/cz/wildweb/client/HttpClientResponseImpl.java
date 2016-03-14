package cz.wildweb.client;

import cz.wildweb.api.HttpClientResponse;

import java.util.HashMap;

public class HttpClientResponseImpl implements HttpClientResponse {

    private final int code;
    private final HashMap<String, String> headers;
    private String body;

    public HttpClientResponseImpl(int code, HashMap<String, String> headers, String body) {
        this.code = code;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public int status() {
        return this.code;
    }

    @Override
    public String header(String name) {
        return this.headers.get(name);
    }

    @Override
    public String body() {
        return this.body;
    }

    @Override
    public String toString() {
        return "HttpClientResponseImpl{" +
                "code=" + code +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
