# WildWeb

Netty-based HTTP server framework. Embeds Thick - a lightweight web server for JRuby.

## Example

```Java
package cz.wildweb.example;

import cz.wildweb.api.HttpServer;
import cz.wildweb.api.WebSocket;
import cz.wildweb.server.netty.NettyServer;

public class Main {

    public static void main(String[] args) {
        HttpServer server = new NettyServer();
        server.start("localhost", 8081);

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

        server.register("/websocket", ((request, response) -> {
            if(request.websocket().valid()) {
                WebSocket socket = request.websocket();
                socket.opened(() -> {
                });
                socket.message(message -> {
                    socket.write(message);
                });
                socket.closed(() -> {
                });

                socket.accept();
            } else {
                response.render("websocket.ftl");
            }
        }));
    }

}

```

or

```Java
package cz.wildweb.example;

import static cz.wildweb.api.Wildweb.*;

public class Main {

    public static void main(String[] args) {
        get("/", (req, res) -> { res.close("Welcome"); });
        startServer(8080);
    }

}

```

or

```Java
package cz.wildweb.example;

import static cz.wildweb.api.Wildweb.*;

public class Main {

    public static void main(String[] args) {
        request(new RootHandler());
        startServer(8080);
    }

}
```

```Java
package cz.wildweb.example;

import cz.wildweb.api.HttpHandler;
import cz.wildweb.api.HttpRequest;
import cz.wildweb.api.HttpResponse;
import cz.wildweb.api.annotations.Request;

@Request(url = "/")
public class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        response.close("Welcome.");
    }
}
```

## Status

Simple prototype, the routing system is everything but optimal, no tests, etc.

## License

WildWeb is released under the MIT License.
