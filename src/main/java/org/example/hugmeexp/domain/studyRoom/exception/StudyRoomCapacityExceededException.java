package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class StudyRoomCapacityExceededException extends BaseCustomException {
    public StudyRoomCapacityExceededException() {
        super(HttpStatus.BAD_REQUEST, "스터디룸 수용 인원을 초과했습니다.", 400);
    }
}