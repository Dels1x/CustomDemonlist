package ua.delsix.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

public class CookieUtil {
    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
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
}
