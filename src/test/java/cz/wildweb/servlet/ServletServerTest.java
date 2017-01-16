package cz.wildweb.servlet;

import cz.wildweb.server.servlet.WildWebServlet;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

public class ServletServerTest {

    private DeploymentInfo servletBuilder;
    private DeploymentManager manager;
    private Undertow server;

    @Before
    public void before() throws ServletException {
        this.servletBuilder = deployment()
                .setClassLoader(WildWebServlet.class.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("test.war")
                .addServlets(servlet("WildWeb servlet", WildWebServlet.class).addMapping(""));

        this.manager = defaultContainer().addDeployment(servletBuilder);
        this.manager.deploy();

        WildWebServlet.getServletServer().register("/", ((request, response) -> {
            response.close("xyz");
        }));

        this.server = Undertow.builder()
                .addHttpListener(getPort(), "localhost")
                .setHandler(this.manager.start())
                .build();
        this.server.start();
    }

    private int getPort() {
        return 9090;
    }

    @After
    public void after() {
        this.server.stop();
    }

    @Test
    public void test() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", getPort()));

        socket.getOutputStream().write(("GET / HTTP/1.1\r\nHost: localhost:" + getPort() + "\r\n\r\n").getBytes());
        socket.getOutputStream().flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Assert.assertEquals("HTTP/1.1 200 OK", reader.readLine());
        Assert.assertEquals("Connection: keep-alive", reader.readLine());
        Assert.assertEquals("Content-Length: 3", reader.readLine());
        reader.readLine();

//        Assert.assertEquals("", reader.readLine());
//        Assert.assertEquals("xyz", reader.readLine());
    }

}
