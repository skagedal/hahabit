package tech.skagedal.hahabit.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "achievements")
public record Achievement(
    @Id Long id,
    LocalDate date
) {
    public static Achievement create(LocalDate date) {
        return new Achievement(null, date);
    }
}
