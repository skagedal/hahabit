package tech.skagedal.hahabit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.model.Trackings;
import tech.skagedal.hahabit.repository.HabitRepository;
import tech.skagedal.hahabit.repository.TrackingRepository;
import tech.skagedal.hahabit.testing.HahabitTest;
import tech.skagedal.hahabit.testing.TestDataManager;

@HahabitTest
class RepositoryTests {
    private final HabitRepository habits;

    private final TrackingRepository trackings;
    private final TestDataManager testDataManager;

    RepositoryTests(@Autowired HabitRepository habits, @Autowired TrackingRepository trackings, @Autowired TestDataManager testDataManager) {
        this.habits = habits;
        this.trackings = trackings;
        this.testDataManager = testDataManager;
    }

    @Test
    void create_habit_and_track_it() {
        final var username = testDataManager.createRandomUser();

        final var habit = habits.save(Habit.create(
            username,
            "Be outside every day"
        ));

        final var fetchedHabit = habits.findById(habit.id()).orElseThrow();
        assertTrue(
            Duration.between(fetchedHabit.createdAt(), Instant.now()).getSeconds() < 5
        );

        trackings.save(Trackings.create(
            LocalDate.of(2023, 1, 5),
            habit.id()
        ));
        trackings.save(Trackings.create(
            LocalDate.of(2023, 1, 6),
            habit.id()
        ));

        final var allTrackings = trackings.findAllByHabitId(habit.id());
        assertEquals(2, allTrackings.size());
    }
}
