package org.example.hugmeexp.domain.studyRoom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 회의실(스터디 홀) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class StudyHallService {

    private final StudyHallRepository studyHallRepository;

    /**
     * 새로운 회의실(스터디 홀)을 생성하고 데이터베이스에 저장합니다.
     * @param requestDto 회의실 생성을 위한 데이터가 담긴 DTO
     * @return 데이터베이스에 저장된 StudyHall 엔티티
     */
    @Transactional
    public StudyHall createStudyHall(StudyHallRequest requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("StudyHallRequest는 null일 수 없습니다.");
        }
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("스터디 홀 이름은 필수입니다.");
        }
        StudyHall studyHall = StudyHall.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .simpleAddress(requestDto.getSimpleAddress())
                .address(requestDto.getAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .thumbnail(requestDto.getThumbnail())
                .openTime(requestDto.getOpenTime())
                .closeTime(requestDto.getCloseTime())
                .build();

        return studyHallRepository.save(studyHall);
    }

    /**
     * 등록된 모든 스터디 홀 목록을 조회합니다.
     * @return StudyHall 엔티티 리스트
     */
    public Page<StudyHall> findAllStudyHalls(Pageable pageable) {
        return studyHallRepository.findAllByIsDeletedFalse(pageable);
    }

    /**
     * @param studyHallId 조회할 스터디 홀의 ID
     * @return 찾아낸 StudyHall 엔티티
     * @throws StudyHallNotFoundException 해당 ID의 홀이 없을 경우
     */
    public StudyHall findStudyHallById(Long studyHallId) {
        return studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));
    }

    /**
     * 특정 스터디 홀의 정보를 수정합니다.
     * @param studyHallId 수정할 스터디 홀의 ID
     * @param requestDto 수정할 정보가 담긴 DTO
     * @return 수정된 StudyHall 엔티티
     */
    @Transactional
    public StudyHall updateStudyHall(Long studyHallId, StudyHallRequest requestDto) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.update(requestDto);
        return studyHall;
    }

    /**
     * 특정 스터디 홀을 삭제합니다.
     * @param studyHallId 삭제할 스터디 홀의 ID
     */
    @Transactional
    public void deleteStudyHall(Long studyHallId) {
        StudyHall studyHall = findStudyHallById(studyHallId);
        studyHall.delete();
    }
}