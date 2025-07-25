package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ReservationAlreadyStartedException extends BaseCustomException {
    public ReservationAlreadyStartedException() {
        super(HttpStatus.BAD_REQUEST, "이미 시작된 예약은 취소할 수 없습니다.", 400);
    }
}