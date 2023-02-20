package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.skagedal.hahabit.testing.Containers;
import tech.skagedal.hahabit.testing.TestDataManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {
    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    private ServletWebServerApplicationContext servletContext;

    private TestDataManager testDataManager;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    @BeforeEach
    void setupTestDataManager() {
        testDataManager = new TestDataManager(userDetailsManager);
    }

    @DynamicPropertySource
    static void registerPostgreSQLProperties(DynamicPropertyRegistry registry) {
        Containers.registerDynamicProperties(registry);
    }

    @Test
    void apis_get_unauthorized_response() {
        final var response = send(GET(uri("/")).build());

        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.headers().firstValue("WWW-Authenticate"))
            .isPresent()
            .hasValueSatisfying(value -> assertThat(value).startsWith("Basic"));
    }

    @Test
    void home_redirects_to_login_in_browsers() {
        final var responseAcceptingHtml = send(GET(uri("/")).header("Accept", "text/html").build());

        assertThat(responseAcceptingHtml.statusCode())
            .isEqualTo(HttpStatus.FOUND.value()); // that's a 302 redirect
        assertThat(responseAcceptingHtml.headers().firstValue("Location"))
            .isPresent()
            .hasValue(uri("/login").toString());
    }


    @Test
    void create_habit() {
        final var username = testDataManager.createRandomUser();

        final var response = send(
            POST(
                uri("/api/habits"),
                """
                   {
                       "description": "Go for a walk"
                   }
                """
            )
                .header("Authorization", testDataManager.authHeader(username))
                .build());

        assertThat(response.statusCode()).isEqualTo(201);
        System.out.println(response.body());
    }

    // Helpers

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + servletContext.getWebServer().getPort() + path);
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpRequest.Builder GET(URI uri) {
        return HttpRequest.newBuilder(uri).GET();
    }

    private static HttpRequest.Builder POST(URI uri, String json) {
        return HttpRequest
            .newBuilder(uri)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .header("content-type", "application/json");
    }
}
