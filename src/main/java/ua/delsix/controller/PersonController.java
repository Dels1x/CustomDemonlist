package ua.delsix.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.exception.UsernameAlreadyExists;
import ua.delsix.jpa.entity.Person;
import ua.delsix.exception.EmailAlreadyExists;
import ua.delsix.service.PersonService;
import ua.delsix.util.ResponseUtil;
import ua.delsix.util.Views;

@RestController
@Log4j2
@RequestMapping("/users")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
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
        } catch (UsernameAlreadyExists | EmailAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        personService.deleteUser(userDetails);
        return ResponseEntity.ok(String.format("User %s was successfully deleted", userDetails.getUsername()));
    }
}
