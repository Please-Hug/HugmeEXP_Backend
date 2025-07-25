package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidReservationTimeException extends BaseCustomException {
    public InvalidReservationTimeException() {
        super(HttpStatus.BAD_REQUEST, "유효하지 않은 예약 시간입니다.", 400);
    }
}