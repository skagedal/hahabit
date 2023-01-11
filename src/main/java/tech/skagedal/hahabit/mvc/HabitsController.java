package tech.skagedal.hahabit.mvc;

import java.security.Principal;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tech.skagedal.hahabit.repository.HabitRepository;

@Controller
public class HabitsController {
    private final HabitRepository habits;

    public HabitsController(HabitRepository habits) {
        this.habits = habits;
    }

    @GetMapping("/habits")
    ModelAndView getHabits(Principal principal) {
        return new ModelAndView(
            "habits",
            Map.of(
                "habits",
                habits.findAllByOwnedBy(principal.getName())
            )
        );
    }
}
