package org.example.hugmeexp.domain.studyRoom.reids.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyHallDeletedEvent {
    private final Long studyHallId;
}
