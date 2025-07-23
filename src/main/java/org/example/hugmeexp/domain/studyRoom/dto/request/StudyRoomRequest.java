package org.example.hugmeexp.domain.studyRoom.dto.request;

import lombok.Getter;

/**
 * 특정 스터디 홀에 새로운 스터디 룸을 생성하기 위한 요청 DTO 입니다.
 */
@Getter
public class StudyRoomRequest {
    private String name;
    private Integer maxNum;
}