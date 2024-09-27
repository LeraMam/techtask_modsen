package org.techtask.authservice.service;

import org.techtask.authservice.dto.UserDTO;
import org.techtask.authservice.request.LoginOrRegisterRequest;

public interface UserService {

    boolean validateToken(String token);

    String login(LoginOrRegisterRequest request);

    UserDTO register(LoginOrRegisterRequest request);
}
