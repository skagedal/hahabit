package tech.skagedal.hahabit.testing;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
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

    public static class RegisterDatasourceExtension implements BeforeAllCallback {
        @Override
        public void beforeAll(ExtensionContext context) {
            final var postgres = postgres();
            System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
            System.setProperty("spring.datasource.username", postgres.getUsername());
            System.setProperty("spring.datasource.password", postgres.getPassword());
        }
    }
}
