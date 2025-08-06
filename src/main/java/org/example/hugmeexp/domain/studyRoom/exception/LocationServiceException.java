package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class LocationServiceException extends BaseCustomException {
    public LocationServiceException(String message) {
        super(HttpStatus.BAD_REQUEST, "위치 서비스 오류: " + message);
    }
}