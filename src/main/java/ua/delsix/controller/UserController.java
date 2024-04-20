package ua.delsix.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.UsernameAlreadyExists;
import ua.delsix.jpa.entity.User;
import ua.delsix.service.UserService;
import ua.delsix.util.ResponseUtil;
import ua.delsix.util.Views;

@RestController
@Log4j2
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    @JsonView(Views.Public.class)
    public ResponseEntity<User> getUser(@RequestParam long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        log.info("New user {} received to create with a password of {}",
                user.getUsername(), user.getPassword());

        try {
            userService.createUser(user);
            return ResponseEntity.ok(String.format("User %s was successfully created", user.getUsername()));
        } catch (UsernameAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.deleteUser(id, userDetails);
            return ResponseEntity.ok(String.format("User %s was successfully deleted", userDetails.getUsername()));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        }
    }
}
