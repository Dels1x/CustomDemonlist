package ua.delsix.controller;

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
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Person;
import ua.delsix.service.AuthService;
import ua.delsix.service.PersonService;
import ua.delsix.util.CookieUtil;
import ua.delsix.util.JwtUtil;
import ua.delsix.util.LogUtil;
import ua.delsix.util.ResponseUtil;

import java.io.IOException;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/oauth2")
public class AuthController {
    private final AuthService authService;
    private final PersonService personService;
    private final JwtUtil jwtUtil;

    private static final String FRONTEND_URL = "http://localhost:3000";

    public AuthController(AuthService authService,
                          PersonService personService,
                          JwtUtil jwtUtil) {
        this.authService = authService;
        this.personService = personService;
        this.jwtUtil = jwtUtil;
    }

    // Gets access token from Discord, on order to generate its own access token later via jwtUtil.generateAccessToken()
    @GetMapping("/callback/discord")
    public void callbackDiscord(@RequestParam(required = false) String code, HttpServletResponse response) {
        if (code == null) {
            LogUtil.codeIsNull();
            return;
        }
        OAuth2Type type = OAuth2Type.DISCORD;
        addFrontendRedirect(response);

        try {
            String accessToken = authService.fetchAccessToken(code, type);
            DiscordUserDto userDto = authService.fetchUserDiscord(accessToken);
            Person person = personService.createUserByDiscordDto(userDto, response, type);
            CookieUtil.attachAuthCookies(
                    response,
                    jwtUtil.generateAccessToken(person),
                    jwtUtil.generateRefreshToken(person));
        } catch (HttpClientErrorException | MissingRequestValueException e) {
            log.error(e.getMessage());
        }
    }

    @GetMapping("/callback/google")
    public void callbackGoogle(
            @RequestParam(required = false) String code,
            HttpServletResponse response) {
        if (code == null) {
            LogUtil.codeIsNull();
            return;
        }
        OAuth2Type type = OAuth2Type.GOOGLE;
        addFrontendRedirect(response);

        try {
            String accessToken = authService.fetchAccessToken(code, type);
            GoogleUserDto userDto = authService.fetchUserGoogle(accessToken);
            Person person = personService.createUserByGoogleDto(userDto, response);

            CookieUtil.attachAuthCookies(
                    response,
                    jwtUtil.generateAccessToken(person),
                    jwtUtil.generateRefreshToken(person));

        } catch (HttpClientErrorException | MissingRequestValueException e) {
            log.error(e.getMessage());
        }
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Request to refresh access token: {}", refreshToken);

        if (refreshToken == null) {
            log.info("No refresh token was found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token is absent"));
        }
        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (AuthorizationException e) {
            return ResponseUtil.userDoesntExistMessage(e.getMessage());
        }
    }

    private void addFrontendRedirect(HttpServletResponse res) {
        res.setHeader("Location", FRONTEND_URL);
        res.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    }
}
