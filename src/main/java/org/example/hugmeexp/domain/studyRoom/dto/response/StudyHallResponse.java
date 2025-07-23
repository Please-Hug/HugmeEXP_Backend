package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 회의실(스터디 홀) 정보 응답을 위한 DTO 입니다.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyHallResponse {
    private Long id;
    private String name;
    private String description;
    private String simpleAddress;
    private String address;
    private Double latitude;
    private Double longitude;
    private String thumbnail;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
}