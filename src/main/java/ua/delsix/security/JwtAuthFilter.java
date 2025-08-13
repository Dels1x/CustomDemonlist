package ua.delsix.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.delsix.service.PersonService;
import ua.delsix.util.JwtUtil;

import java.io.IOException;
import java.util.Set;

@Component
@Log4j2
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final PersonService personService;

    private final Set<String> publicEndpoints = Set.of(
            "/demonlists/demonlist",
            "/users/user",
            "/users/stats",
            "/oauth2/callback",
            "/oauth2/refresh-access-token"
    );

    public JwtAuthFilter(JwtUtil jwtUtil, PersonService personService) {
        this.jwtUtil = jwtUtil;
        this.personService = personService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("New request incoming on path: {}", path);

        boolean isPublicEndpoint = publicEndpoints.stream().anyMatch(path::startsWith);

        // Skip authentication entirely for OAuth2 callback endpoints
        if (path.startsWith("/oauth2/callback") || path.startsWith("/oauth2/refresh-access-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring(7);

            try {
                Claims claims = jwtUtil.validateToken(accessToken);
                String id = claims.getSubject();
                UserDetails userDetails = new CustomUserDetails(personService.getUserById(Long.parseLong(id)));
                log.info("User's name: {}", userDetails.getUsername());
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(userDetails));
            } catch (AuthenticationCredentialsNotFoundException e) {
                log.warn("User's access token is either invalid or expired: {}", e.getMessage());

                // If this is a public endpoint, allow unauthenticated access
                if (isPublicEndpoint) {
                    log.info("Invalid token provided for public endpoint, proceeding without authentication");
                    SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(null));
                } else {
                    // For private endpoints, return 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired access token: " + e.getMessage());
                    return;
                }
            }
        } else {
            // No authorization header provided
            if (isPublicEndpoint) {
                log.info("No token provided for public endpoint, proceeding without authentication");
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(null));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Can't proceed without an access token");
                log.info("User can't proceed without a token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
