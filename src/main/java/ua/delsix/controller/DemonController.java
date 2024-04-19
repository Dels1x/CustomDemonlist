package ua.delsix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.DemonlistDoesntExist;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.service.DemonService;
import ua.delsix.utils.DemonlistUtils;
import ua.delsix.utils.ResponseUtils;

@RestController
@RequestMapping("/demons")
public class DemonController {
    private final DemonService demonService;
    private final DemonlistUtils demonlistUtils;

    public DemonController(DemonService demonService, DemonlistUtils demonlistUtils) {
        this.demonService = demonService;
        this.demonlistUtils = demonlistUtils;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDemon(@RequestParam long demonlistId,
                                              @RequestParam long demonId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonService.deleteDemon(demonlistId, demonId, userDetails);
            return ResponseEntity.ok(String.format(
                    "Demon \"%s\" of demonlist \"%s\" of user \"%s\" has been successfully deleted",
                    demonId,
                    demonlistId,
                    userDetails.getUsername()));
        } catch (AuthorizationException e) {
            return ResponseUtils.authorizationExceptionMessage(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDemon(@RequestBody Demon demon,
                                              @RequestParam long demonlistId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonlistUtils.LinkDemonlistToDemon(demon, demonlistId);
        } catch (DemonlistDoesntExist e) {
            return ResponseUtils.demonlistDoesntExistMessage();
        }

        try {
            demonService.createDemon(demon, userDetails);
            return ResponseEntity.ok(String.format(
                    "New demon \"%s\" of demonlist \"%s\" of user \"%s\" successfully created",
                    demon.getId(),
                    demon.getDemonlist().getId(),
                    userDetails.getUsername()));
        } catch (AuthorizationException e) {
            return ResponseUtils.authorizationExceptionMessage(e.getMessage());
        }
    }
}
