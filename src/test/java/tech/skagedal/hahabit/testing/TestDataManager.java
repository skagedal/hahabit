package tech.skagedal.hahabit.testing;

import java.util.Base64;
import java.util.UUID;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

@Component
public class TestDataManager {
    public static String PASSWORD = "password";

    private final UserDetailsManager userDetailsManager;
    private final Base64.Encoder base64Encoder = Base64.getEncoder();

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

    public String authHeader(String username) {
        return "Basic " + base64Encoder.encodeToString((username + ":" + PASSWORD).getBytes());
    }
}
