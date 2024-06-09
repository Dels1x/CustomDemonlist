package ua.delsix.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.delsix.exception.SamePasswordReset;
import ua.delsix.exception.UsernameAlreadyExists;
import ua.delsix.jpa.entity.User;
import ua.delsix.jpa.repository.DemonRepository;
import ua.delsix.jpa.repository.DemonlistRepository;
import ua.delsix.jpa.repository.UserRepository;
import ua.delsix.dto.PasswordChangeRequest;

@Service
@Log4j2
public class UserService {
    private final DemonlistRepository demonlistRepository;
    private final DemonRepository demonRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(DemonlistRepository demonlistRepository,
                       DemonRepository demonRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.demonlistRepository = demonlistRepository;
        this.demonRepository = demonRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User getUserByUsername(String username) throws EntityNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }

    public User getUserFromUserDetails(UserDetails userDetails) throws EntityNotFoundException {
        return getUserByUsername(userDetails.getUsername());
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

    public void deleteUser(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("User with username " + userDetails.getUsername() + " not found"));
        long id = user.getId();

        demonRepository.deleteByDemonlistUserId(id);
        demonlistRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public void changePassword(PasswordChangeRequest passwordRequest, UserDetails userDetails) throws SamePasswordReset {
        String password = passwordRequest.getPassword();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("User with username " + userDetails.getUsername() + " not found"));
        String oldPassword = user.getPassword();

        if (passwordEncoder.matches(password, oldPassword)) {
            throw new SamePasswordReset();
        }

        user.setPassword(password);
        userRepository.save(user);
    }
}
