package tech.skagedal.hahabit.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "habits")
public record Habit(
    @Id Long id,
    String ownedBy,
    String description,
    @ReadOnlyProperty Instant createdAt
) {
    public static Habit create(String ownedBy, String description) {
        return new Habit(null, ownedBy, description, null);
    }
}
