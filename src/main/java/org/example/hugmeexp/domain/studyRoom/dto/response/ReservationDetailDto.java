package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailDto {

    private Long id;

    private LocalDateTime reservationStart; // 예약 시작 시간

    private LocalDateTime reservationEnd;   // 예약 끝나는 시간

    private Integer partyNum;   // 예약 인원수

    private Integer maxNum;     // 스터디룸 최대 인원 수

    private String studyRoomName;   // 스터디룸 이름

    private String studyHallName;   // 스터디룸 상위 건물 이름

    private String simpleAddress;   // 간단한 주소 ex) 서울시 강남구

    private String address; // 상세한 전체 주소
}
