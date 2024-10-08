package org.techtask.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtGenerator {

    @Value("${app.jwt.expiresIn}")
    public long JWT_EXPIRATION;

    @Value("${app.jwt.key}")
    private String secretKeyString;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (secretKeyString == null || secretKeyString.length() < 64) {
            throw new IllegalArgumentException("Secret key must be at least 64 bytes long.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Token was expired ", ex);
            throw new AuthenticationCredentialsNotFoundException("JWT was expired", ex);
        } catch (JwtException ex) {
            log.warn("Token is incorrect ", ex);
            throw new AuthenticationCredentialsNotFoundException("JWT is incorrect", ex);
        } catch (Exception ex) {
            log.warn("Token validation failed", ex);
            throw new AuthenticationCredentialsNotFoundException("JWT validation failed", ex);
        }
    }
}