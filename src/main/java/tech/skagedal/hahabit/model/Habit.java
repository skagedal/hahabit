package tech.skagedal.hahabit.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "habits")
public record Habit(
    @Id Long id,
    Long ownedBy,
    String description,
    @ReadOnlyProperty Instant createdAt
) {
    public static Habit create(Long ownedBy, String description) {
        return new Habit(null, ownedBy, description, null);
    }
}
