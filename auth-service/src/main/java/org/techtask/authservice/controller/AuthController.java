package org.techtask.authservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.techtask.authservice.dto.TokenDTO;
import org.techtask.authservice.dto.UserDTO;
import org.techtask.authservice.request.LoginOrRegisterRequest;

@RequestMapping("/auth")
public interface AuthController{

    @PostMapping("/validate")
    Boolean validateToken(@RequestBody String token);

    @PostMapping("/register")
    void register(@RequestBody LoginOrRegisterRequest request);

    @PostMapping("/login")
    TokenDTO login(@RequestBody LoginOrRegisterRequest request) ;

}
