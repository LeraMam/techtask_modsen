package org.techtask.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.techtask.authservice.db.entity.UserEntity;
import org.techtask.authservice.db.repository.UserRepository;
import org.techtask.authservice.dto.UserDTO;
import org.techtask.authservice.exception.BadRequestException;
import org.techtask.authservice.mapper.UserMapper;
import org.techtask.authservice.request.LoginOrRegisterRequest;
import org.techtask.authservice.security.JwtGenerator;
import org.techtask.authservice.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UserMapper userMapper;

    public boolean validateToken(String token) {
        return jwtGenerator.validateToken(token);
    }

    public String login(LoginOrRegisterRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtGenerator.generateToken(authentication);
    }

    public UserDTO register(LoginOrRegisterRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new BadRequestException("Login " + request.login() + " is already in use");
        }
        UserEntity user = new UserEntity();
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode((request.password())));
        return userMapper.mapEntityToDto(userRepository.save(user));
    }
}
