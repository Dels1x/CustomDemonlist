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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.delsix.util.CookieUtil;

import java.io.IOException;
import java.util.Objects;

@Component
@Log4j2
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/oauth2/callback")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getAccessTokenFromRequest(request);

        if (accessToken != null) {
            try {
                Claims claims = jwtUtil.validateToken(accessToken);
                String id = claims.getSubject();
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(id));

                filterChain.doFilter(request, response);
                return;
            } catch (AuthenticationCredentialsNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired access token: " + e.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Can't proceed without an access token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        try {
            return Objects.requireNonNull(CookieUtil.findCookie(request, "access-token")).getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
