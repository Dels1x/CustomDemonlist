package ua.delsix.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.client.RestTemplate;
import ua.delsix.dto.DiscordUserDto;

import java.util.Collections;
import java.util.Map;

@Service
public class DiscordOAuthService {
    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.discord.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchAccessTokenFromDiscord(String code) throws MissingRequestValueException {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.put("client_id", Collections.singletonList(clientId));
        body.put("client_secret", Collections.singletonList(clientSecret));
        body.put("code", Collections.singletonList(code));
        body.put("redirect_uri", Collections.singletonList(redirectUri));
        body.put("grant_type", Collections.singletonList("authorization_code"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://discord.com/api/oauth2/token",
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("access_token")) {
            return (String) responseBody.get("access_token");
        } else {
            throw new RuntimeException("Failed to obtain access token from Discord");
        }
    }

    public DiscordUserDto fetchDiscordUser(String accessToken) {
        String url = "https://discord.com/api/users/@me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<DiscordUserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, DiscordUserDto.class);

        return response.getBody();
    }
}
