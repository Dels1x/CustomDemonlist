package ua.delsix.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

public class CookieUtil {
    private CookieUtil() {
        throw new AssertionError("Should not be instantiated");
    }

    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setSecure(false); // Set to true if using HTTPS. currently kept false for development purposes (localhost)
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static String findToken(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null ) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), tokenName)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static void attachAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        attachAccessTokenCookie(response, accessToken);
        attachRefreshTokenCookie(response, refreshToken);
    }

    public static void attachAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addHttpOnlyCookie(response, "access-token", accessToken, JwtUtil.EXPIRATION_TIME / 1000);
    }

    public static void attachRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addHttpOnlyCookie(response, "refresh-token", refreshToken, Integer.MAX_VALUE);
    }
}
