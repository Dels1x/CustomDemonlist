package ua.delsix.util;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.delsix.jpa.entity.User;
import ua.delsix.service.UserService;

@Component
public class UserUtil {
    private final UserService userService;

    public UserUtil(UserService userService) {
        this.userService = userService;
    }

    public User getUserFromUserDetails(UserDetails userDetails) throws EntityNotFoundException {
        return userService.getUserByUsername(userDetails.getUsername());
    }

    public User getUserById(long id) throws EntityNotFoundException {
        return userService.getUserById(id);
    }
}
