package ua.delsix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.service.DemonService;
import ua.delsix.service.DemonlistService;

@RestController
@RequestMapping("/demons")
public class DemonController {
    private final DemonService demonService;
    private final DemonlistService demonlistService;

    public DemonController(DemonService demonService, DemonlistService demonlistService) {
        this.demonService = demonService;
        this.demonlistService = demonlistService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDemon(@RequestBody Demon demon,
                                              @RequestParam long id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Demonlist demonlist = demonlistService.getDemonlistById(id);
        if (demonlist == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provided demonlist doesn't exist");
        }

        demon.setDemonlist(demonlist);

        try {
            demonService.createDemon(demon, userDetails);

            return ResponseEntity.ok(String.format(
                    "New demon \"%s\" of demonlist \"%s\" of user \"%s\" successfully created",
                    demon.getName(),
                    demon.getDemonlist().getName(),
                    userDetails.getUsername()));
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
