package cz.wildweb.server.servlet;

import cz.wildweb.api.HttpHandler;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WildWebServlet extends HttpServlet {

    private static ServletServer SERVLET_SERVER = new ServletServer();

    public static ServletServer getServletServer() {
        return SERVLET_SERVER;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoggerFactory.getLogger(getClass()).info("Handling request to {}", req.getRequestURI());

        ServletServer server = getServletServer();

        ServletRequest request = new ServletRequest(req, server, server.context());
        ServletResponse response = new ServletResponse(resp, request);

        request.readRequest();

        HttpHandler match = server.router().match(request);
        try {
            match.handle(request, response);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Problem processing request", e);
            resp.setStatus(500);
            resp.getWriter().close();
        }
    }

}
