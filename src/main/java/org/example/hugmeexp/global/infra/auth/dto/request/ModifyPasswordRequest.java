package org.example.hugmeexp.global.infra.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifyPasswordRequest {

    @Schema(description = "기존 비밀번호", example = "password123!")
    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~32자로 입력해주세요.")
    private String oldPassword;

    @Schema(description = "새 비밀번호 (영문, 숫자, 특수문자 포함 8~32자)", example = "newPassword123!")
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~32자로 입력해주세요.")
    private String newPassword;

    public static ModifyPasswordRequest of(String oldPassword, String newPassword) {
        return new ModifyPasswordRequest(oldPassword, newPassword);
    }

}
