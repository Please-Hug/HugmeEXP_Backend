package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ReservationConflictException extends BaseCustomException {
    public ReservationConflictException() {
        super(HttpStatus.CONFLICT, "예약 시간이 다른 예약과 겹칩니다.", 409);
    }
}