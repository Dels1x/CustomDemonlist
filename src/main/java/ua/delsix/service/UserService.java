package ua.delsix.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.UsernameAlreadyExists;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.jpa.repository.UserRepository;

@Service
@Log4j2
public class UserService {
    private final AuthorizationService authorizationService;
    private final DemonlistRepository demonlistRepository;
    private final DemonRepository demonRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AuthorizationService authorizationService,
                       DemonlistRepository demonlistRepository,
                       DemonRepository demonRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authorizationService = authorizationService;
        this.demonlistRepository = demonlistRepository;
        this.demonRepository = demonRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(long id) {
        return userRepository.getReferenceById(id);
    }

    public void createUser(User user) throws UsernameAlreadyExists {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userRepository.existsByUsername(user.getUsername())) {
            String errorMessage = String.format("User %s already exists", user.getUsername());
            log.info(errorMessage);
            throw new UsernameAlreadyExists(errorMessage);
        }

        userRepository.save(user);
        log.info("New user {} created", user.getUsername());
    }

    public void deleteUser(long id, UserDetails userDetails) throws AuthorizationException {
        User user = userRepository.findByUsername(userDetails.getUsername());
        authorizationService.verifyUserAuthorization(getUserById(id), user);

        demonRepository.deleteByDemonlistUserId(id);
        demonlistRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }
}
