package cz.wildweb.api;

public interface HttpClientResponse {

    int status();

    String header(String name);

    String body();

}
