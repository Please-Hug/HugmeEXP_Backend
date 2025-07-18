package org.example.hugmeexp.global.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.global.infra.auth.controller.AuthController;
import org.example.hugmeexp.global.infra.auth.dto.request.ModifyPasswordRequest;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.example.hugmeexp.global.infra.auth.exception.PasswordMismatchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.example.hugmeexp.global.security.config.*")
    }
)
@TestPropertySource(properties = {
        "jwt.secret=aHR0cHM6Ly9naXRodWIuY29tL3NldW5nd29vay9qd3QtYXBpLXNlcnZlci1zYW1wbGUteW91LWNhbi11c2UtdGhpcy1sb25nLXNlY3JldC1rZXktZm9yLWVuY3J5cHRpb24K",
        "jwt.access-token-expiration=10000",
        "jwt.refresh-token-expiration=60000"
})
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

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


            // When & Then
            mockMvc.perform(put(BASE_URL + "/password")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
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