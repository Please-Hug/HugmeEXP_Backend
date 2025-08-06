package org.example.hugmeexp.domain.studyRoom.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "예약 시작 시간은 필수입니다.")
    @Future(message = "예약 시작 시간은 미래여야 합니다.")
    private LocalDateTime reservationStart;

    @NotNull(message = "예약 종료 시간은 필수입니다.")
    @Future(message = "예약 종료 시간은 미래여야 합니다.")
    private LocalDateTime reservationEnd;

    @NotNull(message = "예약 인원은 필수입니다.")
    private Integer partyNum;

    @NotNull(message = "스터디룸 ID는 필수입니다.")
    private Long studyRoomId;
}
