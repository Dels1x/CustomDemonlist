package ua.delsix.service;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.client.RestTemplate;
import ua.delsix.dto.DiscordUserDto;
import ua.delsix.dto.GoogleUserDto;
import ua.delsix.enums.OAuth2Type;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.PersonRepository;
import ua.delsix.util.JwtUtil;

import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String discordClientId;

    @Value("${spring.security.oauth2.client.registration.discord.client-secret}")
    private String discordClientSecret;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri}")
    private String discordRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final JwtUtil jwtUtil;
    private final PersonRepository personRepository;

    public AuthService(JwtUtil jwtUtil, PersonRepository personRepository) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
    }

    public String fetchAccessToken(String code, OAuth2Type type) throws MissingRequestValueException {
        String url;
        switch (type) {
            case DISCORD -> url = "https://discord.com/api/oauth2/token";
            case GOOGLE -> url = "https://oauth2.googleapis.com/token";
            default -> throw new RuntimeException("Unknown OAuth2 type");
        }

        HttpEntity<MultiValueMap<String, String>> request = getRequestHttpEntity(code, type);
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("access_token")) {
            return (String) responseBody.get("access_token");
        } else {
            throw new RuntimeException("Failed to obtain access token from " + type);
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getRequestHttpEntity(String code, OAuth2Type type) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        if (type == OAuth2Type.DISCORD) {
            body.add("client_id", discordClientId);
            body.add("client_secret", discordClientSecret);
            body.add("redirect_uri", discordRedirectUri);
        } else if (type == OAuth2Type.GOOGLE) {
            body.add("client_id", googleClientId);
            body.add("client_secret", googleClientSecret);
            body.add("redirect_uri", googleRedirectUri);
        } else {
            throw new RuntimeException("Unknown OAuth2 type");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(body, headers);
    }


    public DiscordUserDto fetchUserDiscord(String accessToken) {
        String url = "https://discord.com/api/users/@me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<DiscordUserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, DiscordUserDto.class);

        log.info("Discord user-info response: {}", response);
        return response.getBody();
    }

    public GoogleUserDto fetchUserGoogle(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserDto.class);

        log.info("Google user-info response: {}", response);
        return response.getBody();
    }

    public String refreshAccessToken(String refreshToken) throws AuthorizationException{
        Claims claims = jwtUtil.validateToken(refreshToken);
        String id = claims.getSubject();
        log.info("id: {}", id);

        Optional<Person> person = personRepository.findById(Long.parseLong(id));
        if (person.isPresent()) {
            log.info("person: {}", person.get());

            return jwtUtil.generateAccessToken(person.get());
        } else {
            throw new AuthorizationException(String.format("User with id of %s doesn't exist", id));
        }
    }

    public void verifyOwnershipOfTheDemonlist(Demonlist demonlist, Person person) throws AuthorizationException {
        if (demonlist == null || !demonlist.getPerson().equals(person)) {
            throw new AuthorizationException("You are not authorized to perform actions with the desired demonlist");
        }
    }

    public void verifyUserAuthorization(Person person1, Person person2) throws AuthorizationException {
        if ((!person1.equals(person2))) {
            throw new AuthorizationException("You are not authorized to perform actions with the desired user");
        }
    }

    public boolean isAuthorized(Person person1, Person person2) {
        return person1.equals(person2);
    }
}
