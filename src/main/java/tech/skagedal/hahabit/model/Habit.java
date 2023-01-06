package tech.skagedal.hahabit.model;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "habits")
public record Habit(
    @Id Long id,
    Long ownedBy,
    String description,
    @ReadOnlyProperty Instant createdAt,
    @MappedCollection(keyColumn = "achieving_habit", idColumn = "id")
    List<Achievement> achievements
) {
    public static Habit create(Long ownedBy, String description) {
        return new Habit(null, ownedBy, description, null, List.of());
    }

    public Habit withAchievements(List<Achievement> newAchievements) {
        return new Habit(
            id, ownedBy, description, createdAt, newAchievements
        );
    }
}
