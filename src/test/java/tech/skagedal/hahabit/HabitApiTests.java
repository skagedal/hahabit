package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.HabitApi;
import org.openapitools.client.model.HabitCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import tech.skagedal.hahabit.testing.HahabitTest;
import tech.skagedal.hahabit.testing.TestDataManager;
import tech.skagedal.hahabit.testing.TestServer;

@HahabitTest
public class HabitApiTests {
    private final TestServer server;
    private final TestDataManager testDataManager;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    public HabitApiTests(@Autowired TestServer server, @Autowired TestDataManager testDataManager) {
        this.server = server;
        this.testDataManager = testDataManager;
    }

    @Test
    void apis_get_unauthorized_response() {
        final var response = send(GET(server.uri("/")).build());

        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.headers().firstValue("WWW-Authenticate"))
            .isPresent()
            .hasValueSatisfying(value -> assertThat(value).startsWith("Basic"));
    }

    @Test
    void home_redirects_to_login_in_browsers() {
        final var responseAcceptingHtml = send(GET(server.uri("/")).header("Accept", "text/html").build());

        assertThat(responseAcceptingHtml.statusCode())
            .isEqualTo(HttpStatus.FOUND.value()); // that's a 302 redirect
        assertThat(responseAcceptingHtml.headers().firstValue("Location"))
            .isPresent()
            .hasValue(server.uri("/login").toString());
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
        return new HabitApi(server.getApiClient(testDataManager.authorizer(username)));
    }

}
