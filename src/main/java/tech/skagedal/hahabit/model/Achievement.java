package tech.skagedal.hahabit.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "achievements")
public record Achievement(
    @Id Long id,
    Long achievingHabit,
    LocalDate date
) {
    public static Achievement create(LocalDate date, Long achievingHabit) {
        return new Achievement(null, achievingHabit, date);
    }
}