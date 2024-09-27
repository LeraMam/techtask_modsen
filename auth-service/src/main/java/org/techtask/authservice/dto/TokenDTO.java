package org.techtask.authservice.dto;

public record TokenDTO(String accessToken, String tokenType) {
    public TokenDTO(String accessToken) {
        this(accessToken, "Bearer ");
    }
}
