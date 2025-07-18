package org.example.hugmeexp.global.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.exception.BaseException;
import org.example.hugmeexp.global.common.exception.code.BaseCode;
import org.example.hugmeexp.global.common.exception.code.ErrorStatus;
import org.example.hugmeexp.global.common.exception.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 어플리케이션 전역에서 발생하는 예외를 처리하는 핸들러
 */
@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        log.warn("BaseException occurred: {}", e.getErrorCode().getMessage(), e);
        BaseCode code = e.getErrorCode();
        return createErrorResponse(code, null);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("Validation Exception occurred: {}", e.getBindingResult().getAllErrors());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        BaseCode errorStatus = ErrorStatus.VALIDATION_ERROR;
        ApiResponse<Map<String, String>> body = ApiResponse.onFailure(errorStatus, errors);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorStatus.getHttpStatus(),
                request
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.warn("AuthorizationDeniedException: {}", e.getMessage());
        BaseCode errorStatus = ErrorStatus.FORBIDDEN;
        return createErrorResponse(errorStatus, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllException(Exception e) {
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);
        BaseCode errorStatus = ErrorStatus.INTERNAL_SERVER_ERROR;
        return createErrorResponse(errorStatus, null);
    }

    private <T> ResponseEntity<ApiResponse<T>> createErrorResponse(BaseCode code, T data) {
        ApiResponse<T> response = ApiResponse.onFailure(code, data);
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }
}
