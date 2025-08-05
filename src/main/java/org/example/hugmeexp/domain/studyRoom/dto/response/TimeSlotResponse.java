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
        String displayTime = startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER);

        return TimeSlotResponse.builder()
                .startTime(startTime)
                .endTime(endTime)
                .available(available)
                .displayTime(displayTime)
                .build();
    }
}