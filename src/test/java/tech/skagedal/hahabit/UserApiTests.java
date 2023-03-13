package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import tech.skagedal.hahabit.testing.HahabitTest;
import tech.skagedal.hahabit.testing.TestDataManager;
import tech.skagedal.hahabit.testing.TestServer;

@HahabitTest
public class UserApiTests {
    private final TestDataManager testDataManager;
    private final TestServer server;

    public UserApiTests(@Autowired TestDataManager testDataManager, @Autowired TestServer server) {
        this.testDataManager = testDataManager;
        this.server = server;
    }

    @Test
    void regular_user_can_not_list_habits() {
        final var username = testDataManager.createRandomUser();
        final var api = userApi(username);

        final var thrown = catchThrowable(api::getUsers);
        assertThat(thrown)
            .isInstanceOf(ApiException.class)
            .matches(e -> ((ApiException) e).getCode() == 403, "is 403 Forbidden");
    }

    @NotNull
    private UserApi userApi(String username) {
        return new UserApi(server.getApiClient(testDataManager.authorizer(username)));
    }

}
