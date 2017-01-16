package cz.wildweb.example;

import cz.wildweb.api.HttpServer;
import cz.wildweb.api.WebSocket;
import cz.wildweb.server.netty.NettyServer;

public class Server {

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
