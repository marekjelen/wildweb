package cz.wildweb.api;

public interface HttpHandler {

    void handle(HttpRequest request, HttpResponse response) throws Exception;

}
