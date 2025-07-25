package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class StudyRoomNotFoundException extends BaseCustomException {
    public StudyRoomNotFoundException() {
        super(HttpStatus.NOT_FOUND, "스터디룸을 찾을 수 없습니다.", 404);
    }
}