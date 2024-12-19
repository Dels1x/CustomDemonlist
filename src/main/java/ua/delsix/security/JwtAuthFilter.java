package ua.delsix.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("path: {}", path);


        if (path.startsWith("/oauth2/callback")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();

        String accessToken = null;
        String refreshToken = null;

        if (cookies != null ) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), "access-token")) {
                    accessToken = cookie.getValue();
                } else if (Objects.equals(cookie.getName(), "refresh-token")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null) {
            try {
                Claims claims = jwtUtil.validateToken(accessToken);
                String id = claims.getSubject();

                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(id));
            } catch (AuthenticationCredentialsNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired access token: " + e.getMessage());
            }
        }

        // TODO
        /* if (refreshToken != null) {
            try {
                Claims claims = jwtUtil.validateToken(refreshToken);
                String id = claims.getSubject();

                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(id));
            } catch (AuthenticationCredentialsNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid refresh token: " + e.getMessage());
            }
        }*/

        filterChain.doFilter(request, response);
    }
}
