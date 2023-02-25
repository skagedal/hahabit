package tech.skagedal.hahabit.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import tech.skagedal.hahabit.model.Trackings;

public interface TrackingRepository extends CrudRepository<Trackings, Long> {
    List<Trackings> findAllByHabitId(Long habitId);
}
