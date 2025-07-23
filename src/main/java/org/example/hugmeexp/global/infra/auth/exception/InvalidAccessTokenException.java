package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.example.hugmeexp.global.common.exception.ErrorCode;

public class InvalidAccessTokenException extends BaseCustomException {
    public InvalidAccessTokenException(ErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getMessage(), errorCode.getStatus().value());
    }
}

