package com.example.auth_project.util;


import com.example.auth_project.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    public String generateAccessToken(User user){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);
        return Jwts.builder()
                .claim("userId",user.getUserId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().toString())
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);
        return Jwts.builder()
                .claim("userId",user.getUserId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().toString())
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateTokenByUserId(String token, String userId){
        try{
            String tokenUserId = extractUserId(token);
            return (tokenUserId.equals(userId) && !isTokenExpired(token));
        }catch (Exception e){
            return false;
        }
    }

    public String extractUsername(String token){
        return extractAllClaims(token).get("username",String.class);
    }

    public String extractUserId(String token){
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token){
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSigningKey(){
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
