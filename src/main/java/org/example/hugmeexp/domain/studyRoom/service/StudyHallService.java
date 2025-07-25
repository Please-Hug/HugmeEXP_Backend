package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyHallService {
    
    private final StudyHallRepository studyHallRepository;
}