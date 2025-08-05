package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTimeResponse {

    // static final로 선언하여 한 번만 생성하고 재사용
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private Long reservationId;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
    private Integer partyNum;
    private String displayTime; // "09:00 - 11:00 (2명)" 형태

    public static ReservationTimeResponse from(StudyRoomReservation reservation) {
        // 미리 생성된 formatter 재사용
        String displayTime = String.format("%s - %s (%d명)",
                reservation.getReservationStart().format(TIME_FORMATTER),
                reservation.getReservationEnd().format(TIME_FORMATTER),
                reservation.getPartyNum());

        return ReservationTimeResponse.builder()
                .reservationId(reservation.getId())
                .reservationStart(reservation.getReservationStart())
                .reservationEnd(reservation.getReservationEnd())
                .partyNum(reservation.getPartyNum())
                .displayTime(displayTime)
                .build();
    }
}