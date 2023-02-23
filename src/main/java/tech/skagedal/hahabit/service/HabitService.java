package tech.skagedal.hahabit.service;

import org.springframework.stereotype.Service;
import tech.skagedal.hahabit.repository.AchievementRepository;
import tech.skagedal.hahabit.repository.HabitRepository;

@Service
public class HabitService {
    private final HabitRepository habits;
    private final AchievementRepository achievements;

    public HabitService(HabitRepository habits, AchievementRepository achievements) {
        this.habits = habits;
        this.achievements = achievements;
    }
}
