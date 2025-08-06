package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationListDto;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;
import org.example.hugmeexp.domain.studyRoom.exception.StudyRoomReservationNotFoundException;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 관리자의 예약 관리 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {

    private final StudyRoomReservationRepository studyRoomReservationRepository;

    /**
     * 모든 예약 목록을 페이징하여 조회합니다.
     * @param pageable 페이징 정보
     * @return 페이징된 예약 목록 DTO
     */
    public Page<ReservationListDto> getAllReservations(Pageable pageable) {
        Page<StudyRoomReservation> reservations = studyRoomReservationRepository.findAllWithDetails(pageable);

        return reservations.map(r -> ReservationListDto.builder()
                .id(r.getId())
                .reservationStart(r.getReservationStart())
                .reservationEnd(r.getReservationEnd())
                .partyNum(r.getPartyNum())
                .studyRoomName(r.getStudyRoom().getName())
                .simpleAddress(r.getStudyRoom().getStudyHall().getSimpleAddress())
                .build());
    }

    /**
     * 특정 예약을 강제로 취소(물리적 삭제)합니다.
     * @param reservationId 취소할 예약의 ID
     * @throws StudyRoomReservationNotFoundException 해당 ID의 예약을 찾을 수 없을 경우
     */
    @Transactional
    public void forceCancelReservation(Long reservationId) {
        if (!studyRoomReservationRepository.existsById(reservationId)) {
            throw new StudyRoomReservationNotFoundException();
        }
        studyRoomReservationRepository.deleteById(reservationId);
    }
}
