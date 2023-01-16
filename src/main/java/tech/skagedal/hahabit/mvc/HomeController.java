package tech.skagedal.hahabit.mvc;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
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
        if (!userOwnsHabitWithId(principal.getName(), achieveForm.habitId())) {
            throw new AccessDeniedException("Unknown habit");
        }
        achievements.save(Achievement.create(
            achieveForm.date(),
            achieveForm.habitId()
        ));
        return new ModelAndView(new RedirectView("/"));
    }

    private boolean userOwnsHabitWithId(String userName, Long habitId) {
        return habits.findById(habitId)
            .map(habit -> Objects.equals(habit.ownedBy(), userName))
            .orElse(false);
    }

    private List<HabitForDate> getHabitsForDate(Principal principal, LocalDate date) {
        return habits.findHabitsForDate(principal.getName(), date);
    }
}
