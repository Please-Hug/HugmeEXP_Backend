package org.example.hugmeexp.global.infra.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.RegisterResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController
{
    private final AuthService authService;

    @Operation(
        summary = "회원 가입",
        description = "회원 가입 요청을 보냅니다. 성공 시 회원 정보 객체를 반환합니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "회원가입 성공",
                content = @Content(schema = @Schema(implementation = RegisterResponse.class))
            )
        }
    )
    @PostMapping("/api/register")
    public ResponseEntity<Response<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request)
    {
        // 서비스에 비즈니스 로직 위임
        RegisterResponse result = authService.registerAndAuthenticate(request);

        return ResponseEntity.status(201).body(Response.<RegisterResponse>builder()
                .message("회원가입에 성공했습니다")
                .data(result)
                .build());
    }
}