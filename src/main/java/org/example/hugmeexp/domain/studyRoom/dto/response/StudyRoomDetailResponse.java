package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;

import java.time.LocalTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoomDetailResponse {
    private Long id;
    private String name;
    private Integer maxNum;
    private String thumbnail;

    // StudyHall 정보 (기존 StudyHallLocationResponse 구조 활용)
    private Long studyHallId;
    private String studyHallName;
    private String description;
    private String simpleAddress;
    private String address;
    private Double latitude;
    private Double longitude;
    private String studyHallThumbnail;
    private LocalTime openTime;
    private LocalTime closeTime;

    // 예약 관련 정보
    private boolean available; // isAvailable로 하려다가 lombok이 isAvailable() getter와 isAvailable(boolean) builder 메서드를 모두 생성하여 컴파일러가 에러띄우길래 일단 이렇게 바꿔놨습니다.
    private int currentReservations;

    public static StudyRoomDetailResponse from(StudyRoom studyRoom) {
        return StudyRoomDetailResponse.builder()
                .id(studyRoom.getId())
                .name(studyRoom.getName())
                .maxNum(studyRoom.getMaxNum())
                .thumbnail(studyRoom.getThumbnail())
                .studyHallId(studyRoom.getStudyHall().getId())
                .studyHallName(studyRoom.getStudyHall().getName())
                .description(studyRoom.getStudyHall().getDescription())
                .simpleAddress(studyRoom.getStudyHall().getSimpleAddress())
                .address(studyRoom.getStudyHall().getAddress())
                .latitude(studyRoom.getStudyHall().getLatitude())
                .longitude(studyRoom.getStudyHall().getLongitude())
                .studyHallThumbnail(studyRoom.getStudyHall().getThumbnail())
                .openTime(studyRoom.getStudyHall().getOpenTime())
                .closeTime(studyRoom.getStudyHall().getCloseTime())
                .available(true) // 기본값, 서비스에서 계산
                .currentReservations(0) // 기본값, 서비스에서 계산
                .build();
    }

    public static StudyRoomDetailResponse from(StudyRoom studyRoom, boolean isAvailable, int currentReservations) {
        StudyRoomDetailResponse response = from(studyRoom);
        return response.toBuilder()
                .available(isAvailable)
                .currentReservations(currentReservations)
                .build();
    }
}