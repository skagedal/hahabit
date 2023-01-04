package tech.skagedal.hahabit.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
public record User(
    @Id
    Long id,
    String email,
    String password,
    LocalDateTime createdAt
) { }
