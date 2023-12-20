package tech.skagedal.hahabit.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tech.skagedal.hahabit.service.HabitService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

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

    @SuppressWarnings("java:S6856") // this seems to not take Spring MVC form controllers into account
    @PostMapping("/habit/{habitId}/{date}/track")
    ModelAndView track(Principal principal, TrackingForm trackingForm) {
        habitService.track(principal, trackingForm.date(), trackingForm.habitId());
        return new ModelAndView(new RedirectView("/"));
    }

}
