package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationListDto {

    private Long id;

    private LocalDateTime reservationStart;

    private LocalDateTime reservationEnd;

    private Integer partyNum;

    private String studyRoomName;

    private String simpleAddress;
}
