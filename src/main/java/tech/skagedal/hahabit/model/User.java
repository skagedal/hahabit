package tech.skagedal.hahabit.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
public record User(
    @Id
    Long id,
    String email,
    String password,
    @ReadOnlyProperty Instant createdAt
) {
    public static User create(String email, String password) {
        return new User(null, email, password, null);
    }
}
