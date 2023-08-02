package tech.skagedal.hahabit.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.model.Trackings;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.TrackingRepository;
import tech.skagedal.hahabit.repository.HabitRepository;

@Service
public class HabitService {
    private final HabitRepository habits;
    private final TrackingRepository trackings;

    public HabitService(HabitRepository habits, TrackingRepository trackings) {
        this.habits = habits;
        this.trackings = trackings;
    }

    public void track(Principal principal, LocalDate date, Long habitId) {
        ownedHabit(principal.getName(), habitId);
        trackings.save(Trackings.create(
            date,
            habitId
        ));
    }

    public void reorderHabits(Principal principal, List<Long> habitIds) {
        var position = 1;
        for (var habitId : habitIds) {
            final var habit = ownedHabit(principal.getName(), habitId);
            habits.save(habit.withPosition(position++));
        }
    }

    private boolean userOwnsHabitWithId(String userName, Long habitId) {
        return habits.findById(habitId)
            .map(habit -> Objects.equals(habit.ownedBy(), userName))
            .orElse(false);
    }

    private Habit ownedHabit(String userName, Long habitId) {
        return habits.findById(habitId)
            .filter(habit -> Objects.equals(habit.ownedBy(), userName))
            .orElseThrow(() -> new AccessDeniedException("Unknown habit"));
    }

    public List<HabitForDate> getHabitsForDate(Principal principal, LocalDate date) {
        return habits.findHabitsForDate(principal.getName(), date);
    }

}
