package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.global.AWS.config.S3Config;
import org.example.hugmeexp.global.common.config.RedisConfig;
import org.example.hugmeexp.global.infra.auth.controller.AuthController;
import org.example.hugmeexp.global.infra.auth.dto.request.ModifyPasswordRequest;
import org.example.hugmeexp.global.infra.auth.exception.PasswordMismatchException;
import org.example.hugmeexp.global.infra.auth.jwt.JwtTokenProvider;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, S3Config.class, RedisConfig.class})
        }
)

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }
    }

    private static final String BASE_URL = "/api/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Nested
    @DisplayName("비밀번호 변경 API 테스트")
    class ModifyPasswordTest {

        @Test
        @DisplayName("비밀번호 변경에 성공한다")
        @WithMockUser(username = "testuser@example.com")
        void modifyPassword_Success() throws Exception {
            // Given
            ModifyPasswordRequest request = ModifyPasswordRequest.of("oldPassword1!", "newPassword1!");
            doNothing().when(authService).modifyPassword(anyString(), any(ModifyPasswordRequest.class));

            // When & Then
            mockMvc.perform(put(BASE_URL + "/password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // Verify
            verify(authService).modifyPassword(eq("testuser@example.com"), any(ModifyPasswordRequest.class));
        }

        @Test
        @DisplayName("기존 비밀번호가 틀리면 비밀번호 변경에 실패한다")
        @WithMockUser(username = "testuser@example.com")
        void modifyPassword_Failure() throws Exception {
            // Given
            ModifyPasswordRequest request = ModifyPasswordRequest.of("wrongPassword1!", "newPassword1!");
            doThrow(new PasswordMismatchException()).when(authService).modifyPassword(anyString(), any(ModifyPasswordRequest.class));

            // When & Then
            mockMvc.perform(put(BASE_URL + "/password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}