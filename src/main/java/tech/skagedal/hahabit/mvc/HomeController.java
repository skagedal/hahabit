package tech.skagedal.hahabit.mvc;

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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.HabitRepository;

@Controller
public class HomeController {
    private final HabitRepository habits;

    public HomeController(HabitRepository habits) {
        this.habits = habits;
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

    private record AchieveForm(String habitId, LocalDate date) {}

    @PostMapping("/achieve")
    View achieve(Principal principal, AchieveForm achieveForm) {
        System.out.println("Achieve: " + achieveForm);
        return new RedirectView("/");
    }

    private List<HabitForDate> getHabitsForDate(Principal principal, LocalDate date) {
        return habits.findHabitsForDate(principal.getName(), date);
    }
}
