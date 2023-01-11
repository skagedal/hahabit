package tech.skagedal.hahabit.mvc;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tech.skagedal.hahabit.model.Habit;

@Controller
public class HabitsController {
    @GetMapping("/habits")
    ModelAndView getHabits() {
        return new ModelAndView(
            "habits",
            Map.of(
                "habits", List.of(
                    Habit.create("simon", "Do things"),
                    Habit.create("simon", "Do other things")
                )
            )
        );
    }
}
