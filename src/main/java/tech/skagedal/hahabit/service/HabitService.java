package tech.skagedal.hahabit.service;

import java.util.Objects;
import org.springframework.stereotype.Service;
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

    public boolean userOwnsHabitWithId(String userName, Long habitId, HomeController homeController) {
        return habits.findById(habitId)
            .map(habit -> Objects.equals(habit.ownedBy(), userName))
            .orElse(false);
    }
}
