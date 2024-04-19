package ua.delsix.utils;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.UserRepository;

@Component
public class UserUtils {
    private final UserRepository userRepository;

    public UserUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserFromUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername());
    }
}
