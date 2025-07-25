package org.example.hugmeexp.domain.studyRoom.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

/**
 * ID로 StudyRoom을 찾을 수 없을 때 발생하는 예외입니다.
 */
public class StudyRoomNotFoundException extends BaseCustomException {
  public StudyRoomNotFoundException(Long roomId) {
    super(HttpStatus.NOT_FOUND, String.format("ID %d에 해당하는 스터디 룸을 찾을 수 없습니다.", roomId));
  }
}