package tech.skagedal.hahabit.api;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.model.HabitForDate;
import tech.skagedal.hahabit.repository.HabitRepository;
import tech.skagedal.hahabit.service.HabitService;

@RestController
public class HabitsApiController {
    private final HabitRepository habits;
    private final HabitService habitService;

    public HabitsApiController(HabitRepository habits, HabitService habitService) {
        this.habits = habits;
        this.habitService = habitService;
    }

    @GetMapping("/api/habits")
    ListHabitsResponse listHabits(Principal principal) {
        return new ListHabitsResponse(
            habits.findAllByOwnedBy(principal.getName())
        );
    }

    private record ListHabitsResponse(List<Habit> habits) { }


    @PostMapping("/api/habits")
    @ResponseStatus(HttpStatus.CREATED)
    EmptyResponse addHabit(@RequestBody HabitCreateRequest request, Principal principal) {
        habits.save(Habit.create(
            principal.getName(),
            request.description()
        ));
        return new EmptyResponse();
    }

    private record HabitCreateRequest(String description) {}

    @GetMapping("/api/habits/{date}")
    ListHabitsForDateResponse listHabitsForDate(Principal principal, @PathVariable LocalDate date) {
        return new ListHabitsForDateResponse(
            habitService.getHabitsForDate(principal, date)
        );
    }

    private record ListHabitsForDateResponse(List<HabitForDate> habits) { }

    @PostMapping("/api/habits/{date}/{habitId}/achieve")
    EmptyResponse achieveHabit(Principal principal, @PathVariable LocalDate date, @PathVariable Long habitId) {
        habitService.achieve(
            principal,
            date,
            habitId
        );
        return new EmptyResponse();
    }

    private record EmptyResponse() { }
}
