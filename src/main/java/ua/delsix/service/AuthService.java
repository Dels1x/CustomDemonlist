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
import ua.delsix.dto.UserDto;
import ua.delsix.enums.OAuth2Type;
import ua.delsix.exception.AuthorizationException;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.jpa.entity.Person;
import ua.delsix.jpa.repository.PersonRepository;
import ua.delsix.util.JwtUtil;

import java.util.Map;

@Service
@Log4j2
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.discord.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri}")
    private String redirectUri;

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

        HttpEntity<MultiValueMap<String, String>> request = getRequestHttpEntity(code);
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

    private HttpEntity<MultiValueMap<String, String>> getRequestHttpEntity(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(body, headers);
    }


    public UserDto fetchUser(String accessToken, OAuth2Type type) {
        String url;
        switch (type) {
            case DISCORD -> url = "https://discord.com/api/users/@me";
            case GOOGLE -> url = "https://www.googleapis.com/oauth2/v3/userinfo";
            default -> throw new RuntimeException("Unknown OAuth2 type");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDto.class);

        return response.getBody();
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims = jwtUtil.validateToken(refreshToken);
        String id = claims.getSubject();
        return jwtUtil.generateAccessToken(personRepository.getReferenceById(Long.parseLong(id)));
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
