package ua.delsix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import ua.delsix.service.DiscordOAuthService;

@RestController
@RequestMapping("/oauth2")
public class LoginController {
    private final DiscordOAuthService discordOAuthService;

    public LoginController(DiscordOAuthService discordOAuthService) {
        this.discordOAuthService = discordOAuthService;
    }

    @GetMapping("/callback/discord")
    public ResponseEntity<?> callbackDiscord(@RequestParam String code) {
        try {
            return ResponseEntity.status(200).body(discordOAuthService.fetchAccessTokenFromDiscord(code));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
