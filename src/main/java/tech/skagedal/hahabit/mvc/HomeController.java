package tech.skagedal.hahabit.mvc;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tech.skagedal.hahabit.model.HabitForDate;

@Controller
public class HomeController {
    @GetMapping("/")
    ModelAndView getHome(Principal principal) {
        return new ModelAndView(
            "home",
            Map.of(
                "date", "2023-01-16",
                "habits", List.of(
                    new HabitForDate(1L, "Eat breakfast", LocalDate.now(), 1L),
                    new HabitForDate(2L, "Shower", LocalDate.now(), null)
                )
            )
        );
    }
}
