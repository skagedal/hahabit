package tech.skagedal.hahabit.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "trackings")
public record Trackings(
    @Id Long id,
    Long habitId,
    LocalDate date
) {
    public static Trackings create(LocalDate date, Long habitId) {
        return new Trackings(null, habitId, date);
    }
}