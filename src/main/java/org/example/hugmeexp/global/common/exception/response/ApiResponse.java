package org.example.hugmeexp.global.common.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.example.hugmeexp.global.common.exception.code.BaseCode;
import org.example.hugmeexp.global.common.exception.code.SuccessStatus;

/**
 * 모든 API 응답을 위한 통합 DTO
 * @param <T> 응답 데이터의 타입
 */
@Getter
@JsonPropertyOrder({"isSuccess", "message", "result"})
public class ApiResponse<T> {

    private final boolean isSuccess;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    private ApiResponse(boolean isSuccess, BaseCode code, T result) {
        this.isSuccess = isSuccess;
        this.message = code.getMessage();
        this.result = result;
    }

    public static <T> ApiResponse<T> onSuccess(BaseCode code, T result) {
        return new ApiResponse<>(true, code, result);
    }

    public static <T> ApiResponse<T> onFailure(BaseCode code, T result) {
        return new ApiResponse<>(false, code, result);
    }
}
