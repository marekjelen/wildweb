package cz.wildweb.api;

import cz.wildweb.client.HttpClientImpl;

import java.util.function.Consumer;

public interface HttpClient {

    static HttpClient get() {
        return new HttpClientImpl();
    }

    HttpClient secure();

    HttpClient open();
    HttpClient open(String host);
    HttpClient open(String host, int port);

    HttpClient method(String method);
    HttpClient uri(String uri);
    HttpClient header(String name, String value);

    HttpClient request(Consumer<HttpClientResponse> callback);

}
