package org.techtask.security;

public interface TokenValidator {
    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}
