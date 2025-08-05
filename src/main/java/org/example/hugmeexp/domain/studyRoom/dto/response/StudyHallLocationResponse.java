package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;

import java.time.LocalTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StudyHallLocationResponse {

    private Long id;
    private String name;
    private String description;
    private String simpleAddress;
    private String address;
    private Double latitude;
    private Double longitude;
    private String thumbnail;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Double distance; // 현재 위치로부터의 거리 (km)
    private Integer availableRooms; // 이용 가능한 스터디룸 수
    private Integer totalRooms; // 전체 스터디룸 수

    // StudyHall 엔티티로부터 생성 (거리 정보 없음)
    public static StudyHallLocationResponse from(StudyHall studyHall) {
        return StudyHallLocationResponse.builder()
                .id(studyHall.getId())
                .name(studyHall.getName())
                .description(studyHall.getDescription())
                .simpleAddress(studyHall.getSimpleAddress())
                .address(studyHall.getAddress())
                .latitude(studyHall.getLatitude())
                .longitude(studyHall.getLongitude())
                .thumbnail(studyHall.getThumbnail())
                .openTime(studyHall.getOpenTime())
                .closeTime(studyHall.getCloseTime())
                .totalRooms(studyHall.getStudyRooms() != null ? studyHall.getStudyRooms().size() : 0)
                .build();
    }

    // 거리 정보와 함께 생성
    public static StudyHallLocationResponse from(StudyHall studyHall, Double distance) {
        StudyHallLocationResponse response = from(studyHall);
        return response.toBuilder()
                .distance(distance)
                .build();
    }

    // 이용 가능한 룸 수 설정
    public StudyHallLocationResponse withAvailableRooms(Integer availableRooms) {
        return this.toBuilder()
                .availableRooms(availableRooms)
                .build();
    }
}