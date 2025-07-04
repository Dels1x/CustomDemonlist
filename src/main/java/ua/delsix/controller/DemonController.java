package ua.delsix.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.dto.DemonDto;
import ua.delsix.enums.Difficulty;
import ua.delsix.exception.*;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.service.DemonService;
import ua.delsix.util.DemonlistUtil;
import ua.delsix.util.ResponseUtil;

import java.time.LocalDate;

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
    public ResponseEntity<String> deleteDemon(@RequestParam long demonId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonService.deleteDemon(demonId, userDetails);
            return ResponseEntity.ok(String.format(
                    "Demon \"%s\" of user \"%s\" has been successfully deleted",
                    demonId,
                    userDetails.getUsername()));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonlistDoesntExistException e) {
            return ResponseUtil.demonlistDoesntExistMessage();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDemon(@RequestBody Demon demon,
                                              @RequestParam long demonlistId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            demonlistUtil.LinkDemonlistToDemon(demon, demonlistId);
        } catch (DemonlistDoesntExistException e) {
            return ResponseUtil.demonlistDoesntExistMessage();
        }

        try {
            Demon newDemon = demonService.createDemon(demon, userDetails);
            return ResponseEntity.ok(newDemon);
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
        log.info("New request to update Demon #{} to position {}", id, position);

        try {
            demonService.updateDemonPosition(id, position, userDetails);
            return ResponseEntity.ok(String.format("%s demon #%s position has been updated", userDetails.getUsername(), id));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseUtil.notFoundMessage(e.getMessage());
        }
    }

    @PatchMapping("/update-name")
    public ResponseEntity<?> updateDemonName(@RequestParam long id,
                                                  @RequestParam String name,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} name to {}", id, name);

        try {
            Demon demon = demonService.updateDemonName(id,name, userDetails);
            return ResponseEntity.ok(demon);
        } catch (InvalidNameException e) {
            return ResponseUtil.invalidName(e.getMessage());
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonDoesntExistMessage();
        }
    }

    @PatchMapping("/update-author")
    public ResponseEntity<?> updateAuthor(@RequestParam long id,
                                               @RequestParam String author,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} author to {}", id, author);

        try {
            Demon demon = demonService.updateDemonAuthor(id, author, userDetails);
            return ResponseEntity.ok(demon);
        } catch (InvalidAuthorException e) {
            return ResponseUtil.invalidName(e.getMessage());
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonlistDoesntExistMessage();
        }
    }

    @PatchMapping("/update-attempts")
    public ResponseEntity<String> updateAttempts(@RequestParam long id,
                                                 @RequestParam int attemptsCount,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} attempts count to {}", id, attemptsCount);

        try {
            demonService.updateDemonAttemptsCount(id, attemptsCount, userDetails);
            return ResponseEntity.ok(String.format("Demon #%d's attempts has been updated to %d", id, attemptsCount));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonDoesntExistMessage();
        }
    }

    @PatchMapping("/update-difficulty")
    public ResponseEntity<String> updateDifficulty(@RequestParam long id,
                                                   @RequestParam Difficulty difficulty,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} difficulty to {}", id, difficulty);

        try {
            demonService.updateDemonDifficulty(id, difficulty, userDetails);
            return ResponseEntity.ok(String.format("Demon #%d's difficulty has been updated to %s", id, difficulty));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonDoesntExistMessage();
        }
    }

    @PatchMapping("/update-enjoyment")
    public ResponseEntity<String> updateEnjoyment(@RequestParam long id,
                                                 @RequestParam int enjoyment,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} enjoyment rating to {}", id, enjoyment);

        try {
            demonService.updateDemonEnjoymentRating(id, enjoyment, userDetails);
            return ResponseEntity.ok(String.format("Demon #%d's enjoyment rating has been updated to %d", id, enjoyment));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonDoesntExistMessage();
        }
    }

    @PatchMapping("/update-completion-date")
    public ResponseEntity<String> updateCompletionDate(@RequestParam long id,
                                                       @RequestParam LocalDate date,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} date of completion to {}", id, date);

        try {
            demonService.updateDemonDateOfCompletion(id, date, userDetails);
            return ResponseEntity.ok(String.format("Demon #%d's date of completion has been updated to %s", id, date));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonDoesntExistMessage();
        }
    }

    @PatchMapping("/update-worst-fail")
    public ResponseEntity<String> updateWorstFail(@RequestParam long id,
                                                  @RequestParam int worstFail,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating demon #{} worst fail to {}", id, worstFail);

        try {
            demonService.updateWorstFail(id, worstFail, userDetails);
            return ResponseEntity.ok(String.format("Demon #%d's worst fail has been updated to %s", id, worstFail));
        } catch (AuthorizationException e) {
            return ResponseUtil.authorizationExceptionMessage(e.getMessage());
        } catch (DemonDoesntExistException e) {
            return ResponseUtil.demonDoesntExistMessage();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countByDemonlist(@RequestParam long demonlistId) {
        int count = demonService.countByDemonlist(demonlistId);
        log.info("Request to count amount of demons in demonlist {}. Count: {}",  demonlistId, count);
        return ResponseEntity.ok(count);
    }
}
