package ua.delsix.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.dto.GoogleUserDto;
import ua.delsix.enums.OAuth2Type;
import ua.delsix.jpa.entity.Person;
import ua.delsix.service.AuthService;
import ua.delsix.service.PersonService;
import ua.delsix.util.CookieUtil;

import java.io.IOException;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/oauth2")
public class AuthController {
    private final AuthService authService;
    private final PersonService personService;
    private static final String FRONTEND_URL = "http://localhost:3000";

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
        OAuth2Type type = OAuth2Type.DISCORD;
        addFrontendRedirect(response);

        try {
            String accessToken = authService.fetchAccessToken(code, type);
            DiscordUserDto userDto = authService.fetchUserDiscord(accessToken);
            Person person = personService.createUserByDiscordDto(userDto, response, type);

            return ResponseEntity.ok(Map.of(
                    "message", "User successfully authenticated and created",
                    "username", person.getUsername(),
                    "id", person.getId()));
        } catch (HttpClientErrorException | MissingRequestValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/callback/google")
    public ResponseEntity<?> callbackGoogle(
            @RequestParam(required = false) String code,
            HttpServletResponse response) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code parameter is missing.");
        }
        OAuth2Type type = OAuth2Type.GOOGLE;
        addFrontendRedirect(response);

        try {
            String accessToken = authService.fetchAccessToken(code, type);
            GoogleUserDto userDto = authService.fetchUserGoogle(accessToken);
            Person person = personService.createUserByGoogleDto(userDto, response, type);
            return ResponseEntity.ok(Map.of(
                    "message", "User successfully authenticated and created",
                    "username", person.getUsername(),
                    "id", person.getId()));
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

    private void addFrontendRedirect(HttpServletResponse httpServletResponse) {
        try {
            httpServletResponse.sendRedirect(FRONTEND_URL);
        } catch (IOException e) {
            log.error(e);
        }
    }
}
