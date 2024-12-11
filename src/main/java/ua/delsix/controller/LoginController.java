package ua.delsix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.jpa.entity.Person;
import ua.delsix.service.DiscordOAuthService;
import ua.delsix.service.PersonService;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class LoginController {
    private final DiscordOAuthService discordOAuthService;
    private final PersonService personService;

    public LoginController(DiscordOAuthService discordOAuthService, PersonService personService) {
        this.discordOAuthService = discordOAuthService;
        this.personService = personService;
    }

    @GetMapping("/callback/discord")
    public ResponseEntity<?> callbackDiscord(@RequestParam String code) {
        try {
            String accessToken = discordOAuthService.fetchAccessTokenFromDiscord(code);
            DiscordUserDto discordUserDto = discordOAuthService.fetchDiscordUser(accessToken);
            Person createdPerson = personService.createUserByDiscordUser(discordUserDto);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "User successfully authenticated and created",
                            "username", createdPerson.getUsername(),
                            "id", createdPerson.getId()));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
