package com.example.auth_project.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {

    @Value("${jwt.refresh-token.cookie-name:refreshToken}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    public ResponseCookie createRefreshTokenCookie(String refreshToken){
        return ResponseCookie.from(refreshTokenCookieName,refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration/1000)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)                        // Expire immediately
                .sameSite("Strict")
                .build();
    }

    public String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }


}
