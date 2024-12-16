package ua.delsix.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
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
    public ResponseEntity<?> callbackDiscord(@RequestParam(required = false) String code, HttpServletResponse response) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code parameter is missing.");
        }

        try {
            String accessToken = discordOAuthService.fetchAccessTokenFromDiscord(code);
            DiscordUserDto discordUserDto = discordOAuthService.fetchDiscordUser(accessToken);
            Person createdPerson = personService.createUserByDiscordUser(discordUserDto);

            Cookie accessTokenCookie = new Cookie("access-token", createdPerson.getAccessToken());
            Cookie refreshTokenCookie = new Cookie("refresh-token", createdPerson.getRefreshToken());

            accessTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            refreshTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            refreshTokenCookie.setPath("/");

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "User successfully authenticated and created",
                            "username", createdPerson.getUsername(),
                            "id", createdPerson.getId()));
        } catch (HttpClientErrorException | MissingRequestValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
