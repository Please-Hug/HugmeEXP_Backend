package org.example.hugmeexp.domain.studyRoom.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 회의실(스터디 홀) 생성을 위한 요청 데이터를 담는 DTO 입니다.
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyHallRequest {

    @NotBlank(message = "스터디 홀 이름은 필수입니다.")
    private String name;

    private String description;

    @NotBlank(message = "간략한 주소는 필수입니다.")
    private String simpleAddress;

    private String address;

    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", message = "위도는 -90에서 90 사이여야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 -90에서 90 사이여야 합니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", message = "경도는 -180에서 180 사이여야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 -180에서 180 사이여야 합니다.")
    private Double longitude;

    private String thumbnail;

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalDateTime openTime;

    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalDateTime closeTime;
}