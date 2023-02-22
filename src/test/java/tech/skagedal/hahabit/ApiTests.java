package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.skagedal.hahabit.http.BodyMapper;
import tech.skagedal.hahabit.testing.Containers;
import tech.skagedal.hahabit.testing.TestDataManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {
    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    private ServletWebServerApplicationContext servletContext;

    @Autowired
    private BodyMapper bodyMapper;

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

        final var habitsBefore = getHabits(username);
        assertThat(habitsBefore).isEmpty();

        createHabit(username, "Go for a walk");

        final var habitsAfter = getHabits(username);
        assertThat(habitsAfter).hasSize(1);
        assertThat(habitsAfter.get(0)).extracting(Habit::description).isEqualTo("Go for a walk");
    }

    // Client methods

    record Habit(Long id, String description) { }

    void createHabit(String username, String description) {
        record Request(String description) { }
        record Response() { }

        final var response = sendReceiving(
            Response.class,
            POST(
                uri("/api/habits"),
                new Request(description)
            )
                .header("Authorization", testDataManager.authHeader(username))
                .build()
        );

        assertThat(response.statusCode()).isEqualTo(201);
    }

    List<Habit> getHabits(String username) {
        record GetHabitsResponse(List<Habit> habits) { }

        final var response = sendReceiving(
            GetHabitsResponse.class,
            GET(uri("/api/habits"))
                .header("Authorization", testDataManager.authHeader(username))
                .build()
        );

        assertThat(response.statusCode()).isEqualTo(200);
        return response.body().habits;
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

    private <T> HttpResponse<T> sendReceiving(Class<T> type, HttpRequest request) {
        try {
            return httpClient.send(request, bodyMapper.receiving(type));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest.Builder GET(URI uri) {
        return HttpRequest.newBuilder(uri).GET();
    }

    private <T> HttpRequest.Builder POST(URI uri, T json) {
        return HttpRequest
            .newBuilder(uri)
            .POST(bodyMapper.sending(json))
            .header("content-type", "application/json");
    }
}
