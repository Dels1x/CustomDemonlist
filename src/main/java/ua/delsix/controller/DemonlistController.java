package ua.delsix.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.dto.DemonlistDto;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.exception.MoreDemonlistsThanAllowed;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.service.DemonlistService;
import ua.delsix.util.ResponseUtil;
import ua.delsix.util.Views;

@RestController
@RequestMapping("/demonlists")
@Log4j2
public class DemonlistController {
    private final DemonlistService demonlistService;

    public DemonlistController(DemonlistService demonlistService) {
        this.demonlistService = demonlistService;
    }

    @GetMapping("/demonlist")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getDemonlist(@RequestParam long id,
                                          @AuthenticationPrincipal @Nullable UserDetails userDetails) {
        try {
            return ResponseEntity.ok(demonlistService.getDemonlistByIdAuth(id, userDetails));
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        }
    }

    @GetMapping("/demonlists")
    @JsonView(Views.Superficial.class)
    public ResponseEntity<?> getDemonlists(@RequestParam long userId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        log.info("New request to get demonlists for user id {}", userId);
        try {
            return ResponseEntity.ok(demonlistService.getDemonlistsByUserId(userId, userDetails));
        } catch (EntityNotFoundException e) {
            log.info("EntityNotFoundException: {}", e.getMessage());
            return ResponseUtil.notFoundMessage(String.format("User %s not found", userId));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createNewDemonlist(@RequestBody Demonlist demonlist,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonlistService.createDemonlist(demonlist, userDetails);
        } catch (MoreDemonlistsThanAllowed e) {
            return ResponseUtil.moreDemonlistsThanAllowed();
        }
        return ResponseEntity.ok(String.format("Demonlist %s has been created", demonlist.getName()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDemonlist(@RequestParam long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonlistService.deleteDemonlist(id, userDetails);
            return ResponseEntity.ok(String.format("Demonlist %s has been deleted", id));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updateDemonlist(@RequestParam long id,
                                                  @RequestBody DemonlistDto dto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonlistService.updateDemonlist(id, dto, userDetails);
            return ResponseEntity.ok(String.format("%s demonlist %s has been updated", userDetails.getUsername(), id));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<String> countDemonlists(@RequestParam long userId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        log.info("New request to count the amount of demonlists of user with an id of {}", userId);

        try {
            return ResponseEntity.ok(String.valueOf(demonlistService.countByPersonId(userId, userDetails)));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }
}
