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
import tech.skagedal.hahabit.model.HabitForDate;
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
    void create_habit_and_track_it() {
        final var username = testDataManager.createRandomUser();

        final var habitsBefore = getHabits(username);
        assertThat(habitsBefore).isEmpty();

        createHabit(username, "Go for a walk");

        final var habitsAfter = getHabits(username);
        assertThat(habitsAfter).hasSize(1);
        final var habit = habitsAfter.get(0);
        assertThat(habit.description()).isEqualTo("Go for a walk");
        assertThat(habit.id()).isNotNull();

        String date = "2020-01-01";

        // Get habits-for-date before tracking
        final var habitsForDateBefore = getHabitsForDate(username, date);
        assertThat(habitsForDateBefore).hasSize(1);
        final var habitForDate = habitsForDateBefore.get(0);
        assertThat(habitForDate.description()).isEqualTo("Go for a walk");
        assertThat(habitForDate.trackingId()).isNull();

        // Track habit
        trackHabit(username, habit.id(), date);

        // Get habits-for-date after tracking
        final var habitsForDateAfter = getHabitsForDate(username, date);
        assertThat(habitsForDateAfter).hasSize(1);
        final var habitForDateAfter = habitsForDateAfter.get(0);
        assertThat(habitForDateAfter.description()).isEqualTo("Go for a walk");
        assertThat(habitForDateAfter.trackingId()).isNotNull();
        assertThat(habitForDateAfter.date()).isEqualTo(date);
    }


    // Client methods

    record Habit(Long id, String description) { }

    private void createHabit(String username, String description) {
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

    private List<Habit> getHabits(String username) {
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

    private void trackHabit(String username, Long habitId, String date) {
        record Request() { }
        record Response() { }

        final var response = sendReceiving(
            Response.class,
            POST(
                uri("/api/habits/" + date + "/" + habitId + "/track"),
                new Request()
            )
                .header("Authorization", testDataManager.authHeader(username))
                .build()
        );

        assertThat(response.statusCode()).isEqualTo(200);
    }

    private List<HabitForDate> getHabitsForDate(String username, String date) {
        record  GetHabitsForDateResponse(List<HabitForDate> habits) { }

        final var response = sendReceiving(
            GetHabitsForDateResponse.class,
            GET(uri("/api/habits/" + date))
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
