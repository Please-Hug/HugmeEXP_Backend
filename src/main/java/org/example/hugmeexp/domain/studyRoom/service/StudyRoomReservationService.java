package org.example.hugmeexp.domain.studyRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.request.ReservationCreateDto;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationDetailDto;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationListDto;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;
import org.example.hugmeexp.domain.studyRoom.exception.*;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomRepository;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomReservationRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyRoomReservationService {
    
    private final StudyRoomReservationRepository studyRoomReservationRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;

    // Lock test 비교용
    @Transactional
    public Long createReservationWithNoLock(ReservationCreateDto createDto, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        StudyRoom studyRoom = studyRoomRepository.findById(createDto.getStudyRoomId())
                .orElseThrow(() -> new StudyRoomNotFoundException(createDto.getStudyRoomId()));

        validateReservationTime(createDto.getReservationStart(), createDto.getReservationEnd());

        if (createDto.getPartyNum() > studyRoom.getMaxNum()) {
            throw new StudyRoomCapacityExceededException();
        }

        checkTimeConflict(studyRoom, createDto.getReservationStart(), createDto.getReservationEnd());

        StudyRoomReservation reservation = StudyRoomReservation.builder()
                .user(user)
                .studyRoom(studyRoom)
                .reservationStart(createDto.getReservationStart())
                .reservationEnd(createDto.getReservationEnd())
                .partyNum(createDto.getPartyNum())
                .build();

        StudyRoomReservation savedReservation = studyRoomReservationRepository.save(reservation);

        return savedReservation.getId();
    }

    @Transactional
    public Long createReservation(ReservationCreateDto createDto, UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(UserNotFoundException::new);

            StudyRoom studyRoom = studyRoomRepository.findByIdWithLock(createDto.getStudyRoomId())
                    .orElseThrow(() -> new StudyRoomNotFoundException(createDto.getStudyRoomId()));

            validateReservationTime(createDto.getReservationStart(), createDto.getReservationEnd());

            if (createDto.getPartyNum() > studyRoom.getMaxNum()) {
                throw new StudyRoomCapacityExceededException();
            }
            checkTimeConflict(studyRoom, createDto.getReservationStart(), createDto.getReservationEnd());

            StudyRoomReservation reservation = StudyRoomReservation.builder()
                    .user(user)
                    .studyRoom(studyRoom)
                    .reservationStart(createDto.getReservationStart())
                    .reservationEnd(createDto.getReservationEnd())
                    .partyNum(createDto.getPartyNum())
                    .build();

            StudyRoomReservation savedReservation = studyRoomReservationRepository.save(reservation);

            return savedReservation.getId();
        } catch (OptimisticLockingFailureException e){
            log.debug("in StudyRoomReservation createReservation, optimistic exception occurred");
            //재시도
            return createReservation(createDto, userDetails);
    }
}

    public ReservationDetailDto getReservationDetail(Long reservationId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        StudyRoomReservation reservation = studyRoomReservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(StudyRoomReservationNotFoundException::new);

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedReservationAccessException();
        }

        return ReservationDetailDto.builder()
                .id(reservation.getId())
                .reservationStart(reservation.getReservationStart())
                .reservationEnd(reservation.getReservationEnd())
                .partyNum(reservation.getPartyNum())
                .studyRoomName(reservation.getStudyRoom().getName())
                .maxNum(reservation.getStudyRoom().getMaxNum())
                .studyHallName(reservation.getStudyRoom().getStudyHall().getName())
                .simpleAddress(reservation.getStudyRoom().getStudyHall().getSimpleAddress())
                .address(reservation.getStudyRoom().getStudyHall().getAddress())
                .build();
    }

    public Page<ReservationListDto> getReservationList(Pageable pageable, UserDetails userDetails) {
        User findUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        Page<StudyRoomReservation> userStudyRoomReservation = studyRoomReservationRepository.findByUser(findUser, pageable);

        return userStudyRoomReservation.map(reservation -> ReservationListDto.builder()
                .id(reservation.getId())
                .reservationStart(reservation.getReservationStart())
                .reservationEnd(reservation.getReservationEnd())
                .partyNum(reservation.getPartyNum())
                .studyRoomName(reservation.getStudyRoom().getName())
                .simpleAddress(reservation.getStudyRoom().getStudyHall().getSimpleAddress())
                .build());
    }

    @Transactional
    public void deleteReservation(Long reservationId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);

        StudyRoomReservation reservation = studyRoomReservationRepository.findById(reservationId)
                .orElseThrow(StudyRoomReservationNotFoundException::new);

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedReservationAccessException();
        }

        if (reservation.getReservationStart().isBefore(LocalDateTime.now())) {
            throw new ReservationAlreadyStartedException();
        }

        studyRoomReservationRepository.delete(reservation);
    }

    /*
    예약을 할 때, 예약이 유효한지 검사
    1. 예약시작(시작, 끝 포함)은 현재 시각보다 뒤 이어야 한다
    2. 시작 시간과, 끝나는 시간은 같으면 안된다
     */
    private void validateReservationTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        
        if (start.isBefore(now) || end.isBefore(now)) {
            throw new InvalidReservationTimeException();
        }
        
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new InvalidReservationTimeException();
        }
    }

    //예약 시간이 다른 예약과 겹치는지 확인한다 (Lock 미활용, 비교용)
    private void checkTimeConflict(StudyRoom studyRoom, LocalDateTime start, LocalDateTime end) {
        List<StudyRoomReservation> existingReservations = 
                studyRoomReservationRepository.findAllByStudyRoomAndEndTimeAfter(studyRoom, LocalDateTime.now());
        
        for (StudyRoomReservation reservation : existingReservations) {
            if (!(end.isBefore(reservation.getReservationStart()) || 
                  start.isAfter(reservation.getReservationEnd()))) {
                throw new ReservationConflictException();
            }
        }
    }
}