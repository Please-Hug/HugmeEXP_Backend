package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class StudyRoomReservationNotFoundException extends BaseCustomException {
    public StudyRoomReservationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "스터디룸 예약을 찾을 수 없습니다.", 404);
    }
}