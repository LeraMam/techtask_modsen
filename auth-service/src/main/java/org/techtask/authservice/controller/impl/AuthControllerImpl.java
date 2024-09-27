package org.techtask.authservice.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.techtask.authservice.controller.AuthController;
import org.techtask.authservice.dto.TokenDTO;
import org.techtask.authservice.dto.UserDTO;
import org.techtask.authservice.request.LoginOrRegisterRequest;
import org.techtask.authservice.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final UserService userService;

    public Boolean validateToken(@RequestBody String token) {
        return userService.validateToken(token);
    }

    public void register(@RequestBody LoginOrRegisterRequest request) {
        userService.register(request);
    }

    public TokenDTO login(@RequestBody LoginOrRegisterRequest request) {
        String token = userService.login(request);
        return new TokenDTO(token);
    }

}
