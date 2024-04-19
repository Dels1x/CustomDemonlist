package ua.delsix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.service.DemonlistService;

@RestController
@RequestMapping("/demonlists")
public class DemonlistController {
    private final DemonlistService demonlistService;

    public DemonlistController(DemonlistService demonlistService) {
        this.demonlistService = demonlistService;
    }

    @GetMapping("/demonlist")
    public ResponseEntity<Demonlist> getDemonlist(@RequestParam long id) {
        return ResponseEntity.ok(demonlistService.getDemonlistById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createNewDemonlist(@RequestBody Demonlist demonlist,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        demonlistService.createDemonlist(demonlist, userDetails);
        return ResponseEntity.ok(String.format("Demonlist %s has been created", demonlist.getName()));
    }
}
