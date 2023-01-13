package tech.skagedal.hahabit.mvc;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tech.skagedal.hahabit.model.Achievement;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.AchievementRepository;
import tech.skagedal.hahabit.repository.HabitRepository;

@Controller
public class HomeController {
    private final HabitRepository habits;
    private final AchievementRepository achievements;

    public HomeController(HabitRepository habits, AchievementRepository achievements) {
        this.habits = habits;
        this.achievements = achievements;
    }

    @GetMapping("/")
    ModelAndView getHome(Principal principal) {
        return new ModelAndView(
            "home",
            Map.of(
                "date", "2023-01-13",
                "habits", getHabitsForDate(
                    principal,
                    LocalDate.of(2023, 1, 13)
                )
            )
        );
    }

    private List<HabitForDate> getHabitsForDate(Principal principal, LocalDate date) {
        return habits.findAllByOwnedBy(principal.getName())
            .stream()
            .map(habit -> new HabitForDate(
                habit.id(),
                habit.description(),
                date,
                achievements.findOneByAchievingHabitAndDate(habit.id(), date)
                    .map(Achievement::id)
                    .orElse(null)
            ))
            .toList();
    }
}
