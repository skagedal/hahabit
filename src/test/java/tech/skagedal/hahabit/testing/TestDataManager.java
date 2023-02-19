package tech.skagedal.hahabit.testing;

import java.util.UUID;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

public class TestDataManager {
    public static String PASSWORD = "password";

    private final UserDetailsManager userDetailsManager;

    public TestDataManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    public String createRandomUser() {
        final var passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var username = UUID.randomUUID().toString();
        final var user = User.builder().passwordEncoder(passwordEncoder::encode)
            .username(username)
            .password(PASSWORD)
            .roles("USER")
            .build();
        userDetailsManager.createUser(user);
        return username;
    }
}
