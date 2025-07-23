package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 스터디 룸 정보 응답을 위한 DTO 입니다.
 */
@Getter
@Builder
public class StudyRoomResponse {
    private Long id;
    private String name;
    private Integer maxNum;
}