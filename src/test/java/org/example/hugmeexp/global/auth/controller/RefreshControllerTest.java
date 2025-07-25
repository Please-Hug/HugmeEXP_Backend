package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.global.infra.auth.dto.request.RefreshRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.example.hugmeexp.config.MockTestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.example.hugmeexp.global.infra.auth.exception.InvalidRefreshTokenException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MockTestConfiguration.class)
@DisplayName("리프레시 토큰 컨트롤러 테스트")
@ActiveProfiles("test")
class RefreshControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("유효한 리프레시 토큰이 주어지면 새로운 access, refresh 토큰을 반환한다.")
    void shouldReturnNewTokens_whenRefreshTokenIsValid() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest("dummy-access-token", "dummy-refresh-token");
        AuthResponse newTokens = new AuthResponse("new-access-token", "new-refresh-token");

        when(authService.refreshTokens(any(RefreshRequest.class))).thenReturn(newTokens);

        // when
        ResultActions result = mockMvc.perform(post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이 주어지면 401 상태코드를 반환한다.")
    void shouldReturnUnauthorized_whenRefreshTokenIsInvalid() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest("fake.access.token", "fake.refresh.token");
        doThrow(new InvalidRefreshTokenException()).when(authService).refreshTokens(any(RefreshRequest.class));

        // when
        ResultActions result = mockMvc.perform(post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isUnauthorized());
    }

//    @Test
//    @DisplayName("아직 만료되지 않은 액세스 토큰으로 리프레시 요청 시 400 상태코드를 반환한다.")
//    void shouldReturnBadRequest_whenAccessTokenIsStillValid() throws Exception {
//        // given
//        credentialService.registerNewUser(new RegisterRequest(username, password, "이순신", phone));
//        AuthResponse tokens = authService.login(new LoginRequest(username, password));
//
//        RefreshRequest request = new RefreshRequest(tokens.getAccessToken(), tokens.getRefreshToken());
//
//        // when
//        ResultActions result = mockMvc.perform(post("/api/refresh")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)));
//
//        // then
//        result.andExpect(status().isBadRequest());
//    }
}