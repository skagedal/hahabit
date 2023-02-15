package tech.skagedal.hahabit.mvc.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.skagedal.hahabit.testing.Containers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HabitRestControllerTest {
    @Autowired
    private ServletWebServerApplicationContext servletContext;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    @DynamicPropertySource
    static void registerPostgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> Containers.postgres().getJdbcUrl());
        registry.add("spring.datasource.username", () -> Containers.postgres().getUsername());
        registry.add("spring.datasource.password", () -> Containers.postgres().getPassword());
    }

    // Tests

    @Test
    void getHome() throws IOException, InterruptedException {
        final var result = httpClient.send(get(uri("/"))
            .header(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .build(), HttpResponse.BodyHandlers.discarding());

        Assertions.assertEquals(HttpStatus.OK, HttpStatus.resolve(result.statusCode()));
        Assertions.assertEquals("text/html;charset=UTF-8", result.headers().firstValue("Content-Type").orElse(null));
    }


    // Helpers

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + servletContext.getWebServer().getPort() + path);
    }

    private static HttpRequest.Builder get(URI uri) {
        return HttpRequest.newBuilder(uri).GET();
    }

}