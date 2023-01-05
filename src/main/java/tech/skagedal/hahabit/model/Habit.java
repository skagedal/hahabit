package tech.skagedal.hahabit.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "habits")
public record Habit(
    @Id
    Long id,
    Long ownedBy,
    String description,

    @ReadOnlyProperty
    Instant createdAt
) {
}
