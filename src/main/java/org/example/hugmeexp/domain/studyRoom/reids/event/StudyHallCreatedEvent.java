package org.example.hugmeexp.domain.studyRoom.reids.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;

@Getter
@AllArgsConstructor
public class StudyHallCreatedEvent {
    private final StudyHall studyHall;
}