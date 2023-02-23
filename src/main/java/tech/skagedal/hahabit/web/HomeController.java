package tech.skagedal.hahabit.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.AchievementRepository;
import tech.skagedal.hahabit.repository.HabitRepository;
import tech.skagedal.hahabit.service.HabitService;

@Controller
public class HomeController {
    private final HabitService habitService;
    private final HabitRepository habits;
    private final AchievementRepository achievements;

    public HomeController(HabitService habitService, HabitRepository habits, AchievementRepository achievements) {
        this.habitService = habitService;
        this.habits = habits;
        this.achievements = achievements;
    }

    @GetMapping("/")
    ModelAndView getHome(Principal principal, @CookieValue(value = "zoneId", defaultValue = "Europe/Stockholm") ZoneId zoneId) {
        final var date = LocalDate.now(zoneId);
        return new ModelAndView(
            "home",
            Map.of(
                "date", date,
                "zoneId", zoneId,
                "habits", getHabitsForDate(principal, date)
            )
        );
    }

    private record AchieveForm(Long habitId, LocalDate date) {}

    @PostMapping("/habit/{habitId}/{date}/achieve")
    ModelAndView achieve(Principal principal, AchieveForm achieveForm) {
        habitService.achieve(principal, achieveForm.date(), achieveForm.habitId());
        return new ModelAndView(new RedirectView("/"));
    }

    private List<HabitForDate> getHabitsForDate(Principal principal, LocalDate date) {
        return habits.findHabitsForDate(principal.getName(), date);
    }
}
