package org.example.hugmeexp.domain.studyRoom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponse {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;
    private String displayTime; // "09:00 - 10:00" 형태

    public static TimeSlotResponse of(LocalDateTime startTime, LocalDateTime endTime, boolean available) {

        // Null 체크
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작 시간과 종료 시간은 필수입니다.");
        }

        // 시간 순서 검증
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        String displayTime = startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER);

        return TimeSlotResponse.builder()
                .startTime(startTime)
                .endTime(endTime)
                .available(available)
                .displayTime(displayTime)
                .build();
    }
}