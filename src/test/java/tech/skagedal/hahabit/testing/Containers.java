package tech.skagedal.hahabit.testing;

import org.springframework.test.context.DynamicPropertyRegistry;
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

    public static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres().getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres().getUsername());
        registry.add("spring.datasource.password", () -> postgres().getPassword());
    }
}
