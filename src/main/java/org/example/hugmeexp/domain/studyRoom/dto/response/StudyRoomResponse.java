package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.*;

/**
 * 스터디 룸 정보 응답을 위한 DTO 입니다.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoomResponse {
    private final Long id;
    private final String name;
    private final Integer maxNum;
}