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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
    private String displayTime; // "09:00 - 10:00" 형태

    public static TimeSlotResponse of(LocalDateTime startTime, LocalDateTime endTime, boolean isAvailable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String displayTime = startTime.format(formatter) + " - " + endTime.format(formatter);

        return TimeSlotResponse.builder()
                .startTime(startTime)
                .endTime(endTime)
                .isAvailable(isAvailable)
                .displayTime(displayTime)
                .build();
    }
}