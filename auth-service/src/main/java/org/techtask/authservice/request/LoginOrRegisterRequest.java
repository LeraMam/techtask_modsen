package org.techtask.authservice.request;

import lombok.Builder;

@Builder
public record LoginOrRegisterRequest(String login, String password) {
}
