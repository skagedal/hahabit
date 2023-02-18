package tech.skagedal.hahabit.testing;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

public class TestDataManager {
    private final UserDetailsManager userDetailsManager;

    public TestDataManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    public void createSimonUser() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var simon = User.builder().passwordEncoder(encoder::encode)
            .username("simon")
            .password("bestpassword")
            .roles("USER")
            .build();
        userDetailsManager.createUser(simon);
    }
}
