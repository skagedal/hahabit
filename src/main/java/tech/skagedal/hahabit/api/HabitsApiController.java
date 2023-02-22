package tech.skagedal.hahabit.api;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.repository.HabitRepository;

@RestController
public class HabitsApiController {
    private final HabitRepository habits;

    public HabitsApiController(HabitRepository habits) {
        this.habits = habits;
    }

    @PostMapping("/api/habits")
    @ResponseStatus(HttpStatus.CREATED)
    HabitCreateResponse addHabit(@RequestBody HabitCreateRequest request, Principal principal) {
        habits.save(Habit.create(
            principal.getName(),
            request.description()
        ));
        return new HabitCreateResponse();
    }

    private record HabitCreateRequest(String description) {}
    private record HabitCreateResponse() {}
}
