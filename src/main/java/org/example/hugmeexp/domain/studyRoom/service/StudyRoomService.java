package org.example.hugmeexp.domain.studyRoom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationTimeResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyRoomDetailResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.TimeSlotResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.exception.StudyRoomNotFoundException;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomRepository;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyHallRepository studyHallRepository;
    private final StudyRoomReservationRepository studyRoomReservationRepository;

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
                .thumbnail(requestDto.getThumbnail())
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

        return studyRoomRepository.findAllByStudyHallAndIsDeletedFalse(parentHall);
    }

    /**
     * 특정 스터디 룸의 정보를 수정합니다.
     *
     * @param studyHallId 부모 스터디 홀의 ID
     * @param roomId      수정할 스터디 룸의 ID
     * @param requestDto  수정할 정보가 담긴 DTO
     * @return 수정된 StudyRoom 엔티티
     */
    @Transactional
    public StudyRoom updateStudyRoom(Long studyHallId, Long roomId, StudyRoomRequest requestDto) {
        StudyHall parentStudyHall = studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        StudyRoom studyRoom = studyRoomRepository.findByIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new StudyRoomNotFoundException(roomId));

        if (!studyRoom.getStudyHall().getId().equals(parentStudyHall.getId())) {
            throw new StudyRoomNotFoundException(roomId);
        }

        studyRoom.update(requestDto);
        return studyRoom;
    }

    /**
     * 특정 스터디 룸을 삭제합니다.
     *
     * @param studyHallId 부모 스터디 홀의 ID
     * @param roomId      삭제할 스터디 룸의 ID
     */
    @Transactional
    public void deleteStudyRoom(Long studyHallId, Long roomId) {
        StudyHall parentStudyHall = studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        StudyRoom studyRoom = studyRoomRepository.findByIdAndIsDeletedFalse(roomId)
                .orElseThrow(() -> new StudyRoomNotFoundException(roomId));

        if (!studyRoom.getStudyHall().getId().equals(parentStudyHall.getId())) {
            throw new StudyRoomNotFoundException(roomId);
        }

        studyRoom.delete();
    }

    /**
     * 특정 스터디홀의 모든 스터디룸 조회 (사용자용)
     */
    public List<StudyRoomDetailResponse> getStudyRoomsInHall(Long studyHallId) {
        StudyHall studyHall = studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)
                .orElseThrow(() -> new StudyHallNotFoundException(studyHallId));

        List<StudyRoom> studyRooms = studyRoomRepository.findAllByStudyHallAndIsDeletedFalse(studyHall);

        return studyRooms.stream()
                .map(room -> {
                    // 현재 시간 기준으로 예약 가능 여부 확인
                    boolean isAvailable = checkRoomAvailability(room.getId());
                    int currentReservations = getCurrentReservationsCount(room.getId());

                    return StudyRoomDetailResponse.from(room, isAvailable, currentReservations);
                })
                .toList();
    }

    /**
     * 특정 스터디룸 상세 정보 조회 (사용자용)
     */
    public StudyRoomDetailResponse getStudyRoomDetail(Long studyRoomId) {
        StudyRoom studyRoom = studyRoomRepository.findByIdAndIsDeletedFalse(studyRoomId)
                .orElseThrow(() -> new StudyRoomNotFoundException(studyRoomId));

        boolean isAvailable = checkRoomAvailability(studyRoomId);
        int currentReservations = getCurrentReservationsCount(studyRoomId);

        return StudyRoomDetailResponse.from(studyRoom, isAvailable, currentReservations);
    }

    /**
     * 특정 스터디룸의 예약 가능한 시간대 조회
     */
    public List<TimeSlotResponse> getAvailableTimeSlots(Long studyRoomId, String dateStr) {
        StudyRoom studyRoom = studyRoomRepository.findByIdAndIsDeletedFalse(studyRoomId)
                .orElseThrow(() -> new StudyRoomNotFoundException(studyRoomId));

        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atTime(9, 0); // 9시부터
        LocalDateTime endOfDay = date.atTime(22, 0);  // 22시까지

        // 해당 날짜의 모든 예약 조회
        List<StudyRoomReservation> reservations = studyRoomReservationRepository
                .findAllByStudyRoomAndReservationStartBetween(studyRoom, startOfDay, endOfDay);

        List<TimeSlotResponse> timeSlots = new ArrayList<>();

        // 1시간 단위로 시간대 생성
        LocalDateTime currentTime = startOfDay;
        while (currentTime.isBefore(endOfDay)) {
            LocalDateTime slotEnd = currentTime.plusHours(1);

            boolean isAvailable = isTimeSlotAvailable(currentTime, slotEnd, reservations);

            timeSlots.add(TimeSlotResponse.of(currentTime, slotEnd, isAvailable));
            currentTime = slotEnd;
        }

        return timeSlots;
    }

    /**
     * 특정 날짜의 스터디룸 예약 현황 조회
     */
    public List<ReservationTimeResponse> getReservationsByDate(Long studyRoomId, String dateStr) {
        StudyRoom studyRoom = studyRoomRepository.findByIdAndIsDeletedFalse(studyRoomId)
                .orElseThrow(() -> new StudyRoomNotFoundException(studyRoomId));

        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<StudyRoomReservation> reservations = studyRoomReservationRepository
                .findAllByStudyRoomAndReservationStartBetween(studyRoom, startOfDay, endOfDay);

        return reservations.stream()
                .map(ReservationTimeResponse::from)
                .sorted((r1, r2) -> r1.getReservationStart().compareTo(r2.getReservationStart()))
                .toList();
    }

    /**
     * 스터디룸의 현재 예약 가능 여부 확인
     */
    private boolean checkRoomAvailability(Long studyRoomId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<StudyRoomReservation> conflictingReservations = studyRoomReservationRepository
                .findConflictingReservations(studyRoomId, now, oneHourLater);

        return conflictingReservations.isEmpty();
    }

    /**
     * 현재 시점의 예약 개수 조회
     */
    private int getCurrentReservationsCount(Long studyRoomId) {
        LocalDateTime now = LocalDateTime.now();

        return studyRoomReservationRepository
                .countCurrentReservations(studyRoomId, now);
    }

    /**
     * 특정 시간대가 예약 가능한지 확인
     */
    private boolean isTimeSlotAvailable(LocalDateTime startTime, LocalDateTime endTime,
                                        List<StudyRoomReservation> reservations) {
        return reservations.stream()
                .noneMatch(reservation ->
                        startTime.isBefore(reservation.getReservationEnd()) &&
                                endTime.isAfter(reservation.getReservationStart())
                );
    }

}