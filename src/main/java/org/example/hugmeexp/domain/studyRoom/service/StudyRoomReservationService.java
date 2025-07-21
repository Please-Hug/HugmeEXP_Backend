package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomReservationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyRoomReservationService {
    
    private final StudyRoomReservationRepository studyRoomReservationRepository;
}