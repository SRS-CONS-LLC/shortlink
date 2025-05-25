package com.srscons.shortlink.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response,  String name, String value, int interval) {
        Cookie tokenCookie = new Cookie(name, value);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true); // Only over HTTPS
        tokenCookie.setDomain(request.getServerName()); // with dot to include subdomains
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(interval); // 1 day

        response.addCookie(tokenCookie);
    }

    public static void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                Cookie deletedCookie = new Cookie(cookie.getName(), null);
                deletedCookie.setPath("/"); // make sure path matches original
                deletedCookie.setDomain(request.getServerName()); // with dot to include subdomains
                deletedCookie.setMaxAge(0); // deletes the cookie
                deletedCookie.setHttpOnly(cookie.isHttpOnly());
                deletedCookie.setSecure(cookie.getSecure());
                response.addCookie(deletedCookie);
            }
        }
    }
}
