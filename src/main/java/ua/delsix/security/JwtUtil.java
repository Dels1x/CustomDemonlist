package ua.delsix.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import ua.delsix.jpa.entity.Person;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    public static final int EXPIRATION_TIME = 1000 * 60 * 60;

    public String generateAccessToken(Person person) {
        return Jwts.builder()
                .subject(person.getId().toString())
                .claim("username", person.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1-hour expiration
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(Person person) {
        return Jwts.builder()
                .subject(person.getId().toString())
                .claim("username", person.getUsername())
                .issuedAt(new Date())
                .signWith(getSecretKey())
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
        } catch(SecurityException | MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while validating the token.", e);
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
