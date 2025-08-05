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
    private Long reservationId;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
    private Integer partyNum;
    private String displayTime; // "09:00 - 11:00 (2명)" 형태

    public static ReservationTimeResponse from(StudyRoomReservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String displayTime = String.format("%s - %s (%d명)",
                reservation.getReservationStart().format(formatter),
                reservation.getReservationEnd().format(formatter),
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