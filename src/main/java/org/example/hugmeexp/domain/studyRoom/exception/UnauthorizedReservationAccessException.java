package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedReservationAccessException extends BaseCustomException {
    public UnauthorizedReservationAccessException() {
        super(HttpStatus.FORBIDDEN, "예약에 대한 접근 권한이 없습니다.", 403);
    }
}