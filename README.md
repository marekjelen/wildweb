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
    }

}
```

## Status

Simple prototype, the routing system is everything but optimal, no tests, etc.

## License

WildWeb is released under the MIT License.