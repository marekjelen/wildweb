package cz.wildweb.api;

public interface HttpHandler {

    public void handle(HttpRequest request, HttpResponse response) throws Exception;

}
