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
import ua.delsix.security.JwtUtil;
import ua.delsix.service.AuthService;
import ua.delsix.service.PersonService;
import ua.delsix.util.CookieUtil;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class AuthController {
    private final AuthService authService;
    private final PersonService personService;

    public AuthController(AuthService authService, PersonService personService) {
        this.authService = authService;
        this.personService = personService;
    }

    @GetMapping("/callback/discord")
    public ResponseEntity<?> callbackDiscord(@RequestParam(required = false) String code, HttpServletResponse response) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code parameter is missing.");
        }

        try {
            String accessToken = authService.fetchAccessTokenFromDiscord(code);
            DiscordUserDto discordUserDto = authService.fetchDiscordUser(accessToken);
            Person createdPerson = personService.createUserByDiscordUser(discordUserDto);

            CookieUtil.addHttpOnlyCookie(response, "access-token", createdPerson.getAccessToken(), JwtUtil.EXPIRATION_TIME);
            CookieUtil.addHttpOnlyCookie(response, "refresh-token", createdPerson.getRefreshToken(), Integer.MAX_VALUE);

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
