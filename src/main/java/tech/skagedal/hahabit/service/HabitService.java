package tech.skagedal.hahabit.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tech.skagedal.hahabit.model.Achievement;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.AchievementRepository;
import tech.skagedal.hahabit.repository.HabitRepository;
import tech.skagedal.hahabit.web.HomeController;

@Service
public class HabitService {
    private final HabitRepository habits;
    private final AchievementRepository achievements;

    public HabitService(HabitRepository habits, AchievementRepository achievements) {
        this.habits = habits;
        this.achievements = achievements;
    }

    public void achieve(Principal principal, LocalDate date, Long habitId) {
        if (!userOwnsHabitWithId(principal.getName(), habitId)) {
            throw new AccessDeniedException("Unknown habit");
        }
        achievements.save(Achievement.create(
            date,
            habitId
        ));
    }

    private boolean userOwnsHabitWithId(String userName, Long habitId) {
        return habits.findById(habitId)
            .map(habit -> Objects.equals(habit.ownedBy(), userName))
            .orElse(false);
    }

    public List<HabitForDate> getHabitsForDate(Principal principal, LocalDate date) {
        return habits.findHabitsForDate(principal.getName(), date);
    }
}
