package tech.skagedal.hahabit;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import tech.skagedal.hahabit.testing.HahabitTest;
import tech.skagedal.hahabit.testing.TestDataManager;
import tech.skagedal.hahabit.testing.TestServer;

import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@HahabitTest
class UserApiTests {
    private final TestDataManager testDataManager;
    private final TestServer server;

    public UserApiTests(@Autowired TestDataManager testDataManager, @Autowired TestServer server) {
        this.testDataManager = testDataManager;
        this.server = server;
    }

    @Test
    void regular_user_can_not_get_user() {
        final var username = testDataManager.createRandomUser();
        final var api = userApi(username);

        assertThatExceptionOfType(ApiException.class)
            .isThrownBy(() -> api.getUser(username))
            .matches(havingStatusCode(403), "is 403 Forbidden");
    }

    @Test
    void admin_user_can_get_user_that_exists() {
        final var username = testDataManager.createAdminUser();
        final var api = userApi(username);

        assertThatNoException()
            .isThrownBy(() -> api.getUser(username));
    }

    @Test
    void admin_user_get_404_for_user_that_does_not_exist() {
        final var username = testDataManager.createAdminUser();
        final var api = userApi(username);

        assertThatExceptionOfType(ApiException.class)
            .isThrownBy(() -> api.getUser(UUID.randomUUID().toString()))
            .matches(havingStatusCode(404), "is 404 Not Found");
    }

    @NotNull
    private UserApi userApi(String username) {
        return new UserApi(server.getApiClient(testDataManager.authorizer(username)));
    }

    private static Predicate<ApiException> havingStatusCode(int code) {
        return exception -> exception.getCode() == code;
    }
}
