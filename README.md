# WildWeb

Netty-based HTTP server framework.

## Example

```
package cz.wildweb.example;

import cz.wildweb.api.HttpServer;
import cz.wildweb.impl.HttpServerImpl;

public class Main {

    public static void main(String[] args) {
        HttpServer server = new HttpServerImpl();
        server.start("localhost", 8080);

        server.register("/", (request, response) -> {
            response.close("Hello");
        });

        server.register("/echo/:content", (request, response) -> {
            response.close(request.attribute("content"));
        });

        server.register("/print/*", (request, response) -> {
            response.close(request.splat());
        });

        server.register("/print/*/inside", (request, response) -> {
            response.close("Inside: " + request.splat());
        });
    }

}
```

## Status

Simple prototype, the routing system is everything but optimal, no tests, etc.

## License

WildWeb is released under the MIT License.