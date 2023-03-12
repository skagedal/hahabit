package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.HabitApi;
import org.openapitools.client.model.HabitCreateRequest;
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
public class HabitApiTests {
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
    void create_habit_and_track_it() throws ApiException {
        final var username = testDataManager.createRandomUser();

        final var api = habitApi(username);

        final var habitsBefore = api.getHabits();
        assertThat(habitsBefore.getHabits()).isEmpty();

        final var habitCreateRequest = new HabitCreateRequest();
        habitCreateRequest.setDescription("Go for a walk");
        api.createHabit(habitCreateRequest);

        final var habitsAfter = api.getHabits();
        assertThat(habitsAfter.getHabits()).hasSize(1);
        final var habit = habitsAfter.getHabits().get(0);
        assertThat(habit.getDescription()).isEqualTo("Go for a walk");
        assertThat(habit.getId()).isNotNull();

        final var date = LocalDate.of(2020, 1, 1);

        final var habitsForDateBefore = api.getHabitsForDate(date);
        assertThat(habitsForDateBefore.getHabits()).hasSize(1);
        final var habitForDate = habitsForDateBefore.getHabits().get(0);
        assertThat(habitForDate.getDescription()).isEqualTo("Go for a walk");
        assertThat(habitForDate.getTrackingId()).isNull();

        // Track habit
        api.trackHabit(date, habit.getId());

        // Get habits-for-date after tracking
        final var habitsForDateAfter = api.getHabitsForDate(date);
        assertThat(habitsForDateAfter.getHabits()).hasSize(1);
        final var habitForDateAfter = habitsForDateAfter.getHabits().get(0);
        assertThat(habitForDateAfter.getDescription()).isEqualTo("Go for a walk");
        assertThat(habitForDateAfter.getTrackingId()).isNotNull();
        assertThat(habitForDateAfter.getDate()).isEqualTo(date);
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

    private HttpRequest.Builder GET(URI uri) {
        return HttpRequest.newBuilder(uri).GET();
    }

    // Generated API

    @NotNull
    private HabitApi habitApi(String username) {
        return new HabitApi(new ApiClient()
            .setRequestInterceptor(builder -> builder.header("Authorization", testDataManager.authHeader(username)))
            .setScheme("http")
            .setHost("127.0.0.1")
            .setPort(servletContext.getWebServer().getPort()));
    }
}
