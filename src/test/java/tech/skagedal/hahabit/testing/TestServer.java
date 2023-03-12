package tech.skagedal.hahabit.testing;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.function.Consumer;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TestServer {
    private final ServletWebServerApplicationContext servletContext;

    public TestServer(@Autowired ServletWebServerApplicationContext servletContext) {
        this.servletContext = servletContext;
    }

    public URL url(String path) {
        try {
            return uri(path).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URI uri(String path) {
        return URI.create("http://127.0.0.1:" + servletContext.getWebServer().getPort() + path);
    }

    public ApiClient getApiClient(Consumer<HttpRequest.Builder> authorization) {
        return new ApiClient()
            .setRequestInterceptor(authorization)
            .setScheme("http")
            .setHost("127.0.0.1")
            .setPort(servletContext.getWebServer().getPort());
    }
}
