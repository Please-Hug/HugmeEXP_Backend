package org.example.hugmeexp.global.infra.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.global.infra.auth.dto.request.ModifyPasswordRequest;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<Response> modifyPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ModifyPasswordRequest request) {
      Boolean result = authService.modifyPassword(userDetails.getUsername(), request);

      // TODO: 예외 처리 변경
      if (result != true) {
        return ResponseEntity.badRequest().build();
      }
        return ResponseEntity.ok().build();
    }
}
