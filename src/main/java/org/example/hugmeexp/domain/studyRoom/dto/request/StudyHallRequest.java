package org.example.hugmeexp.domain.studyRoom.dto.request;

import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 회의실(스터디 홀) 생성을 위한 요청 데이터를 담는 DTO 입니다.
 */
@Getter
public class StudyHallRequest {

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