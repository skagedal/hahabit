package tech.skagedal.hahabit.testing;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

public class TestDataManager {
    private final UserDetailsManager userDetailsManager;

    public TestDataManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    public void createSimonUser() {
        final var simon = User.withDefaultPasswordEncoder()
            .username("simon")
            .password("bestpassword")
            .roles("USER")
            .build();
        userDetailsManager.createUser(simon);
    }
}
