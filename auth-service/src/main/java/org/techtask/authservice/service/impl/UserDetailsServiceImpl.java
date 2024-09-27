package org.techtask.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.techtask.authservice.db.entity.UserEntity;
import org.techtask.authservice.db.repository.UserRepository;
import org.techtask.authservice.exception.BadRequestException;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity appUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new BadRequestException("Login " + login + " not found"));
        return new User(appUser.getLogin(), appUser.getPassword(), new ArrayList<>());
    }
}
