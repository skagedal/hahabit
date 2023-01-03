package tech.skagedal.hahabit.testing;

import org.testcontainers.containers.PostgreSQLContainer;

public class Containers {
    private static PostgreSQLContainer<?> postgreSQLContainer;

    synchronized public static PostgreSQLContainer<?> postgres() {
        if (postgreSQLContainer == null) {
            postgreSQLContainer = new PostgreSQLContainer<>("postgres:12.12")
                .withDatabaseName("hahabit")
                .withUsername("test")
                .withPassword("password")
                .withLabel("reuse-label", "hahabit")
                .withReuse(true);
            postgreSQLContainer.start();
        }
        return postgreSQLContainer;
    }
}
