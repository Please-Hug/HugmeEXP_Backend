package org.example.hugmeexp.global.auth.controller;

import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.infra.auth.service.CredentialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.example.hugmeexp.config.MockTestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MockTestConfiguration.class)
@DisplayName("로그아웃 컨트롤러 테스트")
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private AuthService authService;
    @Autowired private CredentialService credentialService;
    @Autowired private UserService userService;


    private final String username = "logoutuser";
    private final String phone = "010-2222-3333";
    private final String password = "logout123!";

    @AfterEach
    void tearDown() {
        userService.deleteByUsername(username);
    }

    @Test
    @DisplayName("액세스 토큰을 포함한 요청으로 로그아웃하면 200 상태코드를 반환한다.")
    void shouldReturnOk_whenLogoutSuccessfully() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest(username, password, "강호동", phone);
        credentialService.registerNewUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthResponse tokens = authService.login(loginRequest);

        // when
        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                // then
                .andExpect(status().isOk());
    }
}