package tech.skagedal.hahabit.model;

import java.time.LocalDate;
import org.springframework.lang.Nullable;

public record HabitForDate(
    Long habitId,
    String description,
    LocalDate date,
    @Nullable Long trackingId
) {
    public boolean isTracked() {
        return trackingId != null;
    }
}
