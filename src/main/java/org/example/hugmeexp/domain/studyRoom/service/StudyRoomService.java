package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyRoomService {
    
    private final StudyRoomRepository studyRoomRepository;
}