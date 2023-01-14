package tech.skagedal.hahabit.mvc;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.AchievementRepository;
import tech.skagedal.hahabit.repository.HabitRepository;

@Controller
public class HomeController {
    private final HabitRepository habits;

    public HomeController(HabitRepository habits) {
        this.habits = habits;
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
        return habits.findHabitsForDate(principal.getName(), date);
    }
}
