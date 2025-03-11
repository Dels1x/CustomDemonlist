package ua.delsix.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.dto.DemonDto;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.DemonlistDoesntExist;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.service.DemonService;
import ua.delsix.util.DemonlistUtil;
import ua.delsix.util.ResponseUtil;

@RestController
@RequestMapping("/demons")
@Log4j2
public class DemonController {
    private final DemonService demonService;
    private final DemonlistUtil demonlistUtil;

    public DemonController(DemonService demonService, DemonlistUtil demonlistUtil) {
        this.demonService = demonService;
        this.demonlistUtil = demonlistUtil;
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
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDemon(@RequestBody Demon demon,
                                              @RequestParam long demonlistId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonlistUtil.LinkDemonlistToDemon(demon, demonlistId);
        } catch (DemonlistDoesntExist e) {
            return ResponseUtil.demonlistDoesntExistMessage();
        }

        try {
            demonService.createDemon(demon, userDetails);
            return ResponseEntity.ok(String.format(
                    "New demon #%s of demonlist #%s of user \"%s\" successfully created",
                    demon.getId(),
                    demon.getDemonlist().getId(),
                    userDetails.getUsername()));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateDemon(@RequestParam long id,
                                              @RequestBody DemonDto dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonService.updateDemon(id, dto, userDetails);
            return ResponseEntity.ok(String.format("%s demon #%s has been updated", userDetails.getUsername(), id));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseUtil.illegalArgument(e.getMessage());
        }
    }

    @PatchMapping("/update-position")
    public ResponseEntity<String> updateDemonPosition(@RequestParam long id,
                                                      @RequestParam int position,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonService.updateDemonPosition(id, position, userDetails);
            return ResponseEntity.ok(String.format("%s demon #%s position has been updated", userDetails.getUsername(), id));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countByDemonlist(@RequestParam long demonlistId) {
        int count = demonService.countByDemonlist(demonlistId);
        log.info("Request to count amount of demons in demonlist {}. Count: {}",  demonlistId, count);
        return ResponseEntity.ok(count);
    }
}
