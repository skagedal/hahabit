package tech.skagedal.hahabit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import tech.skagedal.hahabit.model.Achievement;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.model.User;
import tech.skagedal.hahabit.repository.HabitRepository;
import tech.skagedal.hahabit.repository.UserRepository;
import tech.skagedal.hahabit.testing.Containers;

@SpringBootTest
class HahabitApplicationTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    HabitRepository habitRepository;

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
        final var simon = userRepository.save(
            User.create(
                "skagedal@gmail.com",
                "bestpassword"
            ));

        final var fetchedSimon = userRepository.findById(simon.id()).orElseThrow();

        assertEquals(
            simon.email(),
            fetchedSimon.email()
        );
    }

    @Test
    @Transactional
    void createHabit() {
        final var user = createExampleUser();

        final var habit = habitRepository.save(Habit.create(
            user.id(),
            "Be outside every day"
        ));

        final var fetchedHabit = habitRepository.findById(habit.id()).orElseThrow();

        System.out.println(habit);
        System.out.println(fetchedHabit);
    }

    @Test
    @Transactional
    void createHabitWithAchievements() {
        final var user = createExampleUser();
        final var habit = habitRepository.save(Habit.create(
            user.id(),
            "Be outside every day"
        ));
        final var achievedHabit = habitRepository.save(habit.withAchievements(
            List.of(Achievement.create(LocalDate.of(2023, 1, 6)))
        ));

        assertEquals(1, achievedHabit.achievements().size());
    }

    @NotNull
    private User createExampleUser() {
        return userRepository.save(User.create("skagedal@gmail.com", "bestpassword"));
    }
}
