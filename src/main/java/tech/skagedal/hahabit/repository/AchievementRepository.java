package tech.skagedal.hahabit.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import tech.skagedal.hahabit.model.Achievement;
import tech.skagedal.hahabit.model.Habit;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findAllByAchievingHabit(Long habitId);
}
