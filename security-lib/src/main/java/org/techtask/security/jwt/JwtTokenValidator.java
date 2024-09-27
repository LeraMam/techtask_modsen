package org.techtask.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.techtask.security.TokenValidator;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Qualifier("jwtTokenValidator")
@RequiredArgsConstructor
public class JwtTokenValidator implements TokenValidator {
    @Value("${app.jwt.key}")
    private String secretKeyString;
    @Value("${auth.validation.url}")
    private String url;
    private final RestTemplate restTemplate;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
                if (secretKeyString == null || secretKeyString.length() < 64) {
            throw new IllegalArgumentException("Secret key must be at least 64 bytes long.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Boolean isValid = restTemplate.postForObject(url, token, Boolean.class);

        if (isValid == null || !isValid) {
            throw new RuntimeException("JWT was exprired or incorrect");
        }
        return true;
    }
}
