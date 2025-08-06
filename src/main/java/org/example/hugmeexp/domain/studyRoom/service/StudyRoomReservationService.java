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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

    /**
     * 락 없이 스터디룸 예약을 생성하는 메서드 (성능 테스트 및 비교용)
     * @param createDto 예약 생성 요청 데이터
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 예약의 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws StudyRoomNotFoundException 스터디룸을 찾을 수 없는 경우
     * @throws StudyRoomCapacityExceededException 인원수가 스터디룸 최대 수용 인원을 초과하는 경우
     * @throws InvalidReservationTimeException 예약 시간이 유효하지 않은 경우
     * @throws ReservationConflictException 다른 예약과 시간이 겹치는 경우
     */
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

    /**
     * 낙관적 락을 사용하여 스터디룸 예약을 생성하는 메서드
     * OptimisticLockingFailureException 발생 시 자동으로 재시도합니다.
     * @param createDto 예약 생성 요청 데이터
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 예약의 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws StudyRoomNotFoundException 스터디룸을 찾을 수 없는 경우
     * @throws StudyRoomCapacityExceededException 인원수가 스터디룸 최대 수용 인원을 초과하는 경우
     * @throws InvalidReservationTimeException 예약 시간이 유효하지 않은 경우
     * @throws ReservationConflictException 다른 예약과 시간이 겹치는 경우
     */
    @Retryable(
            value = {OptimisticLockingFailureException.class},  //낙관적Lock 예외 발생시, 재시도
            maxAttempts = 3,    //최대 시도수
            backoff = @Backoff(delay = 100, multiplier = 2) //100ms 단위로 재시도, 이후 대기시간 2배씩 증가
    )
    @Transactional
    public Long createReservation(ReservationCreateDto createDto, UserDetails userDetails) {

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
    }

    /**
     * 예약 상세 정보를 조회하는 메서드
     * 요청한 사용자가 해당 예약의 소유자인지 권한을 검증합니다.
     * @param reservationId 조회할 예약의 ID
     * @param userDetails 인증된 사용자 정보
     * @return 예약 상세 정보 DTO
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws StudyRoomReservationNotFoundException 예약을 찾을 수 없는 경우
     * @throws UnauthorizedReservationAccessException 해당 예약에 접근 권한이 없는 경우
     */
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

    /**
     * 사용자의 예약 목록을 페이징하여 조회하는 메서드
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬 기준)
     * @param userDetails 인증된 사용자 정보
     * @return 사용자의 예약 목록 페이지
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 예약을 삭제하는 메서드
     * 예약 소유자 권한과 예약 시작 시간을 검증한 후 삭제합니다.
     * @param reservationId 삭제할 예약의 ID
     * @param userDetails 인증된 사용자 정보
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     * @throws StudyRoomReservationNotFoundException 예약을 찾을 수 없는 경우
     * @throws UnauthorizedReservationAccessException 해당 예약에 접근 권한이 없는 경우
     * @throws ReservationAlreadyStartedException 이미 시작된 예약은 삭제할 수 없는 경우
     */
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