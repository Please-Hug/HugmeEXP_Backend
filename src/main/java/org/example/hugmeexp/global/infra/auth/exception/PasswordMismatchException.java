package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

// TOOO 나중에 예외처리 로직 변경 예장
public class PasswordMismatchException extends BaseCustomException {
    public PasswordMismatchException() {
        super(HttpStatus.BAD_REQUEST, "현재 비밀번호가 틀렸습니다.", 400);
    }
}
