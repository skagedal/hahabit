package tech.skagedal.hahabit;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import tech.skagedal.hahabit.model.User;
import tech.skagedal.hahabit.repository.UserRepository;
import tech.skagedal.hahabit.testing.Containers;

@SpringBootTest
class HahabitApplicationTests {
    @Autowired
    UserRepository repository;

    @DynamicPropertySource
    static void registerPostgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> Containers.postgres().getJdbcUrl());
        registry.add("spring.datasource.username", () -> Containers.postgres().getUsername());
        registry.add("spring.datasource.password", () -> Containers.postgres().getPassword());
    }

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void createUser() {
        final var simon = new User(null, "skagedal@gmail.com", "bestpassword", LocalDateTime.now());
        final var savedSimon = repository.save(simon);

        final var fetchedSimon = repository.findById(savedSimon.id()).orElseThrow();

        Assertions.assertEquals(
            simon.email(),
            fetchedSimon.email()
        );
    }
}
