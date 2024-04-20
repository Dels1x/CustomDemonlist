package ua.delsix.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.UserRepository;

@Component
public class UserUtil {
    private final UserRepository userRepository;

    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserFromUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername());
    }
}
