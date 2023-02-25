package tech.skagedal.hahabit.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tech.skagedal.hahabit.model.Habit;
import tech.skagedal.hahabit.model.HabitForDate;

public interface HabitRepository extends CrudRepository<Habit, Long> {
    List<Habit> findAllByOwnedBy(String user);
    @Query("""
        SELECT habit.id AS habit_id, habit.description,
               (SELECT id as tracking_id
                FROM trackings t
                WHERE t.habit_id = habit.id AND t.date = :date)
        FROM habits habit
        WHERE habit.owned_by = :user;
        """)
    List<HabitForDate> findHabitsForDate(
        @Param("user") String user,
        @Param("date") LocalDate date
    );
}
