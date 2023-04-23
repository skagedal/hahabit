package tech.skagedal.hahabit.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tech.skagedal.hahabit.service.HabitService;

@Controller
public class HabitTrackerController {
    private final HabitService habitService;

    public HabitTrackerController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping("/")
    ModelAndView getHome(Principal principal, @CookieValue(value = "zoneId", defaultValue = "Europe/Stockholm") ZoneId zoneId) {
        final var date = LocalDate.now(zoneId);
        return new ModelAndView(
            "home",
            Map.of(
                "date", date,
                "zoneId", zoneId,
                "habits", habitService.getHabitsForDate(principal, date)
            )
        );
    }

    private record TrackingForm(Long habitId, LocalDate date) {}

    @PostMapping("/habit/{habitId}/{date}/track")
    ModelAndView track(Principal principal, TrackingForm trackingForm) {
        habitService.track(principal, trackingForm.date(), trackingForm.habitId());
        return new ModelAndView(new RedirectView("/"));
    }

}
