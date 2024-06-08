package ua.delsix.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.exception.SamePasswordReset;
import ua.delsix.exception.UsernameAlreadyExists;
import ua.delsix.jpa.entity.User;
import ua.delsix.dto.PasswordChangeRequest;
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
    public ResponseEntity<?> getUser(@RequestParam long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok(String.format("User %s was successfully created", user.getUsername()));
        } catch (UsernameAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordChangeRequest passwordRequest, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.changePassword(passwordRequest, userDetails);
            return ResponseEntity.ok(String.format("%s's password has been successfully changed.", userDetails.getUsername()));
        } catch (SamePasswordReset e) {
            return ResponseUtil.samePasswordReset();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails);
        return ResponseEntity.ok(String.format("User %s was successfully deleted", userDetails.getUsername()));
    }
}
