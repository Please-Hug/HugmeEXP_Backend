package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

/**
 * ID로 StudyHall을 찾을 수 없을 때 발생하는 예외입니다.
 */
public class StudyHallNotFoundException extends BaseCustomException {
    public StudyHallNotFoundException(Long studyHallId) {
        super(HttpStatus.NOT_FOUND, String.format("ID %d에 해당하는 스터디 홀을 찾을 수 없습니다.", studyHallId));
    }
}