package tech.skagedal.hahabit.api;

import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersApiController {
    private final UserDetailsManager userDetailsManager;

    public UsersApiController(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users")
    ListUsersResponse listUsers(Principal principal) {
        return new ListUsersResponse(List.of());
    }

    private record ListUsersResponse(List<User> users) {}
    private record User(String username) {}
}
