package org.techtask.authservice.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.authservice.controller.impl.AuthControllerImpl;
import org.techtask.authservice.request.LoginOrRegisterRequest;
import org.techtask.authservice.security.JwtGenerator;
import org.techtask.authservice.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AuthControllerImpl.class)
public class AuthControllerImplTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtGenerator jwtGenerator;

    @Test
    @WithMockUser(value = "spring")
    public void loginTest() throws Exception {
        LoginOrRegisterRequest request = new LoginOrRegisterRequest("user", "password123");
        String token = "mockedJwtToken";

        given(userService.login(request)).willReturn(token);
        given(jwtGenerator.generateToken(any())).willReturn(token);

        ResultActions response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.accessToken").value(token));
    }

    @Test
    @WithMockUser(value = "spring")
    public void validateTokenTest() throws Exception {
        String token = "validToken";
        given(userService.validateToken(token)).willReturn(true);

        ResultActions response = mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token)
                .with(csrf()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser(value = "spring")
    public void registrationTest() throws Exception {
        LoginOrRegisterRequest request =
                new LoginOrRegisterRequest("newUser", "newPassword123");

        ResultActions response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()));
        response.andExpect(status().isOk())
                .andDo(print());

        verify(userService).register(request);
    }
}