package tech.skagedal.hahabit.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import tech.skagedal.hahabit.model.Achievement;
import tech.skagedal.hahabit.model.Habit;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findAllByAchievingHabit(Long habitId);
    Optional<Achievement> findOneByAchievingHabitAndDate(Long id, LocalDate date);
}
