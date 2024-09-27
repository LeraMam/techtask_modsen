package org.techtask.authservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.authservice.db.entity.UserEntity;
import org.techtask.authservice.db.repository.UserRepository;
import org.techtask.authservice.request.LoginOrRegisterRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void validateTestSuccess() throws Exception {
        UserEntity user = UserEntity.builder().login("userLogin").password(passwordEncoder.encode("userPassword")).build();
        LoginOrRegisterRequest request = LoginOrRegisterRequest.builder().login("userLogin").password("userPassword").build();
        userRepository.save(user);

        ResultActions loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(200));

        String responseBody = loginResult.andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(token))
                .andExpect(status().is(200));
    }

    @Test
    public void validateTokenInvalidTestFail() throws Exception {
        UserEntity user = UserEntity.builder().login("userLogin").password(passwordEncoder.encode("userPassword")).build();
        LoginOrRegisterRequest request = LoginOrRegisterRequest.builder().login("userLogin").password("userPassword").build();
        userRepository.save(user);

        ResultActions loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(200));

        String responseBody = loginResult.andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        String failToken = token + "hfgjdj";

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(failToken))
                .andExpect(status().is(404));
    }

    @Test
    public void loginTestSuccess() throws Exception {
        UserEntity user = UserEntity.builder().login("userLogin").password(passwordEncoder.encode("userPassword")).build();
        LoginOrRegisterRequest request = LoginOrRegisterRequest.builder().login("userLogin").password("userPassword").build();
        UserEntity savedUser = userRepository.save(user);

        List<UserEntity> userEntityList = userRepository.findAll();
        assertEquals(1, userEntityList.size());
        UserEntity userEntity = userEntityList.get(0);
        assertEquals(savedUser.getLogin(), userEntity.getLogin());

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(200));

        resultActions.andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @ParameterizedTest
    @MethodSource("loginRequestFailArguments")
    public void loginTestFail(LoginOrRegisterRequest request) throws Exception {
        UserEntity user = UserEntity.builder().login("userLogin").password(passwordEncoder.encode("userPassword")).build();
        UserEntity savedUser = userRepository.save(user);

        List<UserEntity> userEntityList = userRepository.findAll();
        assertEquals(1, userEntityList.size());
        UserEntity userEntity = userEntityList.get(0);
        assertEquals(savedUser.getLogin(), userEntity.getLogin());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void registrationTestSuccess() throws Exception {
        LoginOrRegisterRequest request = LoginOrRegisterRequest.builder().login("userLogin").password("userPassword").build();

        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(200));

        List<UserEntity> userEntityList = userRepository.findAll();
        assertEquals(1, userEntityList.size());
        UserEntity userEntity = userEntityList.get(0);
        assertEquals(request.login(), userEntity.getLogin());
        assertNotEquals(request.password(), userEntity.getPassword());
    }

    @Test
    public void registrationTestFail() throws Exception {
        UserEntity user = UserEntity.builder().login("userLogin").password(passwordEncoder.encode("userPassword")).build();
        userRepository.save(user);

        LoginOrRegisterRequest request = LoginOrRegisterRequest.builder().login("userLogin").password("userPassword").build();
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(400));
    }

    private static List<LoginOrRegisterRequest> loginRequestFailArguments() {
        return List.of(
                LoginOrRegisterRequest.builder().login("userLogin").password("userFailPassword").build(),
                LoginOrRegisterRequest.builder().login("userFailLogin").password("userPassword").build()
        );
    }
}
