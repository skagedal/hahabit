package tech.skagedal.hahabit.api;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsersApiController {
    private static Logger logger = LoggerFactory.getLogger(UsersApiController.class);
    private final UserDetailsManager userDetailsManager;

    public UsersApiController(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/{username}")
    User listUsers(Principal principal, @PathVariable String username) {
        logger.info("Checking user {}", username);
        if (userDetailsManager.userExists(username)) {
            return new User(username);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private record User(String username) {}
}
