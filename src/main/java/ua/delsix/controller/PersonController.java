package ua.delsix.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.dto.UserStatsDto;
import ua.delsix.exception.UsernameAlreadyExistsException;
import ua.delsix.jpa.entity.Person;
import ua.delsix.exception.EmailAlreadyExistsException;
import ua.delsix.service.DemonlistService;
import ua.delsix.service.PersonService;
import ua.delsix.util.ResponseUtil;
import ua.delsix.util.Views;

@RestController
@Log4j2
@RequestMapping("/users")
public class PersonController {
    private final PersonService personService;
    private final DemonlistService demonlistService;

    public PersonController(PersonService personService,
                            DemonlistService demonlistService) {
        this.personService = personService;
        this.demonlistService = demonlistService;
    }

    @GetMapping("/user")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getUser(@RequestParam long id) {
        try {
            return ResponseEntity.ok(personService.getUserById(id));
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody Person person) {
        try {
            personService.createUser(person);
            return ResponseEntity.ok(String.format("User %s was successfully created", person.getUsername()));
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        personService.deleteUser(userDetails);
        return ResponseEntity.ok(String.format("User %s was successfully deleted", userDetails.getUsername()));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestParam long id) {
        log.info("New request to get stats for user id {}", id);

        try {
            UserStatsDto stats = demonlistService.getUserStats(id);
            return ResponseEntity.ok(stats);
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }
}
