package org.example.hugmeexp.domain.studyRoom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyHallRepository studyHallRepository;

    /**
     * 특정 스터디 홀에 새로운 룸을 생성합니다.
     *
     * @param studyHallId 룸을 추가할 부모 스터디 홀의 ID
     * @param requestDto  생성할 룸의 정보
     * @return 저장된 StudyRoom 엔티티
     */
    @Transactional
    public StudyRoom createStudyRoom(Long studyHallId, StudyRoomRequest requestDto) {
        StudyHall parentStudyHall = studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        StudyRoom studyRoom = StudyRoom.builder()
                .name(requestDto.getName())
                .maxNum(requestDto.getMaxNum())
                .studyHall(parentStudyHall)
                .build();

        return studyRoomRepository.save(studyRoom);
    }

    /**
     * 특정 스터디 홀에 속한 모든 룸 목록을 조회합니다.
     *
     * @param studyHallId 조회할 스터디 홀의 ID
     * @return StudyRoom 엔티티 리스트
     */
    public List<StudyRoom> findAllRoomsInHall(Long studyHallId) {
        StudyHall parentHall = studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        return studyRoomRepository.findAllByStudyHall(parentHall);
    }
}