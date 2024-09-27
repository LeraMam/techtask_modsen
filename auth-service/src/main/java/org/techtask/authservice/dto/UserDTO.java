package org.techtask.authservice.dto;

import lombok.Builder;

@Builder
public record UserDTO(Long id,
                      String login,
                      String password) {
}
