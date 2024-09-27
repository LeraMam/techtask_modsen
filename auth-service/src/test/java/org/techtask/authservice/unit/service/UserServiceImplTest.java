package org.techtask.authservice.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.authservice.db.entity.UserEntity;
import org.techtask.authservice.db.repository.UserRepository;
import org.techtask.authservice.dto.UserDTO;
import org.techtask.authservice.exception.BadRequestException;
import org.techtask.authservice.mapper.UserMapper;
import org.techtask.authservice.request.LoginOrRegisterRequest;
import org.techtask.authservice.security.JwtGenerator;
import org.techtask.authservice.service.impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtGenerator jwtGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void registerTestSuccess() {
        LoginOrRegisterRequest request = new LoginOrRegisterRequest("testUser", "password123");
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin("testUser");
        userEntity.setPassword("encodedPassword");

        UserDTO userDTO = UserDTO.builder().login("testUser").password("encodedPassword").build();

        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(userMapper.mapEntityToDto(any())).thenReturn(userDTO);
        when(userRepository.save(any())).thenReturn(userEntity);

        UserDTO savedUserDTO = userService.register(request);

        assertNotNull(savedUserDTO);
        assertEquals("testUser", savedUserDTO.login());
    }

    @Test
    public void registerTestFail() {
        LoginOrRegisterRequest request = new LoginOrRegisterRequest("testUser", "password123");

        when(userRepository.existsByLogin(request.login())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.register(request));
        assertEquals("Login testUser is already in use", exception.getMessage());
    }

    @Test
    public void loginTestSuccess() {
        LoginOrRegisterRequest request = new LoginOrRegisterRequest("testUser", "password123");
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String expectedToken = "mockedJwtToken";
        when(jwtGenerator.generateToken(authentication)).thenReturn(expectedToken);

        String resultToken = userService.login(request);

        assertNotNull(resultToken);
        assertEquals(expectedToken, resultToken);
    }

    @Test
    public void validateTokenTestSuccess() {
        String token = "validToken";
        when(jwtGenerator.validateToken(token)).thenReturn(true);

        boolean isValid = userService.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    public void validateTokenTestFail() {
        String token = "invalidToken";
        when(jwtGenerator.validateToken(token)).thenReturn(false);

        boolean isValid = userService.validateToken(token);
        assertFalse(isValid);
    }
}
