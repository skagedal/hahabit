package tech.skagedal.hahabit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import tech.skagedal.hahabit.model.Achievement;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.model.User;
import tech.skagedal.hahabit.repository.AchievementRepository;
import tech.skagedal.hahabit.repository.HabitRepository;
import tech.skagedal.hahabit.repository.UserRepository;
import tech.skagedal.hahabit.testing.Containers;

@SpringBootTest
class HahabitApplicationTests {
    @Autowired
    UserRepository users;

    @Autowired
    HabitRepository habits;

    @Autowired
    AchievementRepository achievements;

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
        final var simon = users.save(
            User.create(
                "skagedal@gmail.com",
                "bestpassword"
            ));

        final var fetchedSimon = users.findById(simon.id()).orElseThrow();

        assertEquals(
            simon.email(),
            fetchedSimon.email()
        );
    }

    @Test
    @Transactional
    void createHabitAndAchievement() {
        final var user = users.save(User.create("skagedal2@gmail.com", "bestpassword"));

        final var habit = habits.save(Habit.create(
            user.id(),
            "Be outside every day"
        ));

        final var fetchedHabit = habits.findById(habit.id()).orElseThrow();
        assertTrue(
            Duration.between(fetchedHabit.createdAt(), Instant.now()).getSeconds() < 5
        );

        achievements.save(Achievement.create(
            LocalDate.of(2023, 1, 5),
            habit.id()
        ));
        achievements.save(Achievement.create(
            LocalDate.of(2023, 1, 6),
            habit.id()
        ));

        final var allAchievements = achievements.findAllByAchievingHabit(habit.id());
        assertEquals(2, allAchievements.size());
    }
}
