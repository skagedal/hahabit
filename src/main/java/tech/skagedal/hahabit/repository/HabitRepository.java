package tech.skagedal.hahabit.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import tech.skagedal.hahabit.model.Habit;

public interface HabitRepository extends CrudRepository<Habit, Long> {
    List<Habit> findAllByOwnedBy(String user);
}
