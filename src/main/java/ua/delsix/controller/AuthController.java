package ua.delsix.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.jpa.entity.Person;
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

    // Gets access token from Discord, on order to generate its own access token later via jwtUtil.generateAccessToken()
    @GetMapping("/callback/discord")
    public ResponseEntity<?> callbackDiscord(@RequestParam(required = false) String code, HttpServletResponse response) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code parameter is missing.");
        }

        try {
            String accessToken = authService.fetchAccessTokenFromDiscord(code);
            DiscordUserDto discordUserDto = authService.fetchDiscordUser(accessToken);
            Person createdPerson = personService.createUserByDiscordUser(discordUserDto, response);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "User successfully authenticated and created",
                            "username", createdPerson.getUsername(),
                            "id", createdPerson.getId()));
        } catch (HttpClientErrorException | MissingRequestValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletResponse response, HttpServletRequest request) {
        String refreshToken = CookieUtil.findToken(request, "refresh-token");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is absent");
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);
        CookieUtil.attachAccessTokenCookie(response, newAccessToken);

        return ResponseEntity.ok("Access token successfully refreshed");
    }
}
