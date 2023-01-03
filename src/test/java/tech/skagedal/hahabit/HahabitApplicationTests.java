package tech.skagedal.hahabit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.skagedal.hahabit.testing.Containers;

@SpringBootTest
class HahabitApplicationTests {
    @DynamicPropertySource
    static void registerPostgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> Containers.postgres().getJdbcUrl());
        registry.add("spring.datasource.username", () -> Containers.postgres().getUsername());
        registry.add("spring.datasource.password", () -> Containers.postgres().getPassword());
    }

    @Test
    void contextLoads() {
    }
}
