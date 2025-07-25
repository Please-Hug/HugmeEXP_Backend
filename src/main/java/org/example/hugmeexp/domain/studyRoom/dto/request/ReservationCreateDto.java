package org.example.hugmeexp.domain.studyRoom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreateDto {

    private LocalDateTime reservationStart;

    private LocalDateTime reservationEnd;

    private Integer partyNum;

    private Long studyRoomId;
}
