package org.example.hugmeexp.domain.studyRoom.service;

import org.example.hugmeexp.domain.studyRoom.dto.request.ReservationCreateDto;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationDetailDto;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationListDto;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;
import org.example.hugmeexp.domain.studyRoom.exception.*;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomRepository;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomReservationRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StudyRoomReservationService 테스트")
class StudyRoomReservationServiceTest {

    @InjectMocks
    private StudyRoomReservationService studyRoomReservationService;

    @Mock
    private StudyRoomReservationRepository studyRoomReservationRepository;

    @Mock
    private StudyRoomRepository studyRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @Mock
    private User testUser;
    private StudyHall testStudyHall;
    private StudyRoom testStudyRoom;
    private StudyRoomReservation testReservation;
    private ReservationCreateDto testCreateDto;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;

    @BeforeEach
    void setUp() {
        futureStart = LocalDateTime.now().plusDays(1);
        futureEnd = futureStart.plusHours(2);

        when(testUser.getId()).thenReturn(1L);
        when(testUser.getUsername()).thenReturn("testuser");
        when(testUser.getName()).thenReturn("테스트 사용자");

        testStudyHall = StudyHall.builder()
                .id(1L)
                .name("테스트 스터디홀")
                .simpleAddress("서울시 강남구")
                .address("서울시 강남구 테스트로 123")
                .build();

        testStudyRoom = StudyRoom.builder()
                .id(1L)
                .name("테스트 스터디룸")
                .maxNum(4)
                .studyHall(testStudyHall)
                .build();

        testReservation = StudyRoomReservation.builder()
                .id(1L)
                .user(testUser)
                .studyRoom(testStudyRoom)
                .reservationStart(futureStart)
                .reservationEnd(futureEnd)
                .partyNum(2)
                .build();

        testCreateDto = ReservationCreateDto.builder()
                .studyRoomId(1L)
                .reservationStart(futureStart)
                .reservationEnd(futureEnd)
                .partyNum(2)
                .build();

        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testStudyRoom));
        when(studyRoomReservationRepository.findAllByStudyRoomAndEndTimeAfter(eq(testStudyRoom), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(studyRoomReservationRepository.save(any(StudyRoomReservation.class))).thenReturn(testReservation);

        // when
        Long reservationId = studyRoomReservationService.createReservation(testCreateDto, userDetails);

        // then
        assertThat(reservationId).isEqualTo(1L);
        
        ArgumentCaptor<StudyRoomReservation> captor = ArgumentCaptor.forClass(StudyRoomReservation.class);
        verify(studyRoomReservationRepository).save(captor.capture());
        StudyRoomReservation savedReservation = captor.getValue();
        
        assertThat(savedReservation.getUser()).isEqualTo(testUser);
        assertThat(savedReservation.getStudyRoom()).isEqualTo(testStudyRoom);
        assertThat(savedReservation.getReservationStart()).isEqualTo(futureStart);
        assertThat(savedReservation.getReservationEnd()).isEqualTo(futureEnd);
        assertThat(savedReservation.getPartyNum()).isEqualTo(2);
    }

    @Test
    @DisplayName("예약 생성 실패 - 사용자를 찾을 수 없음")
    void createReservation_fail_userNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            studyRoomReservationService.createReservation(testCreateDto, userDetails);
        });
        
        verify(studyRoomRepository, never()).findByIdWithLock(any());
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 스터디룸을 찾을 수 없음")
    void createReservation_fail_studyRoomNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyRoomNotFoundException.class, () -> {
            studyRoomReservationService.createReservation(testCreateDto, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 최대 수용 인원 초과")
    void createReservation_fail_capacityExceeded() {
        // given
        ReservationCreateDto exceededDto = ReservationCreateDto.builder()
                .studyRoomId(1L)
                .reservationStart(futureStart)
                .reservationEnd(futureEnd)
                .partyNum(5) // maxNum(4)보다 큰 값
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testStudyRoom));

        // when & then
        assertThrows(StudyRoomCapacityExceededException.class, () -> {
            studyRoomReservationService.createReservation(exceededDto, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 과거 시간 예약")
    void createReservation_fail_pastTime() {
        // given
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        ReservationCreateDto pastDto = ReservationCreateDto.builder()
                .studyRoomId(1L)
                .reservationStart(pastTime)
                .reservationEnd(pastTime.plusHours(2))
                .partyNum(2)
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testStudyRoom));

        // when & then
        assertThrows(InvalidReservationTimeException.class, () -> {
            studyRoomReservationService.createReservation(pastDto, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 시작 시간과 종료 시간이 같음")
    void createReservation_fail_sameStartEndTime() {
        // given
        ReservationCreateDto sameTimeDto = ReservationCreateDto.builder()
                .studyRoomId(1L)
                .reservationStart(futureStart)
                .reservationEnd(futureStart) // 시작 시간과 동일
                .partyNum(2)
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testStudyRoom));

        // when & then
        assertThrows(InvalidReservationTimeException.class, () -> {
            studyRoomReservationService.createReservation(sameTimeDto, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 실패 - 예약 시간 충돌")
    void createReservation_fail_timeConflict() {
        // given
        StudyRoomReservation existingReservation = StudyRoomReservation.builder()
                .reservationStart(futureStart.minusMinutes(30))
                .reservationEnd(futureStart.plusMinutes(30))
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testStudyRoom));
        when(studyRoomReservationRepository.findAllByStudyRoomAndEndTimeAfter(eq(testStudyRoom), any(LocalDateTime.class)))
                .thenReturn(List.of(existingReservation));

        // when & then
        assertThrows(ReservationConflictException.class, () -> {
            studyRoomReservationService.createReservation(testCreateDto, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 생성 - OptimisticLockingFailureException 발생 시 재시도")
    void createReservation_retryOnOptimisticLockFailure() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findByIdWithLock(1L))
                .thenThrow(new OptimisticLockingFailureException("Lock failed"))
                .thenReturn(Optional.of(testStudyRoom));
        when(studyRoomReservationRepository.findAllByStudyRoomAndEndTimeAfter(eq(testStudyRoom), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(studyRoomReservationRepository.save(any(StudyRoomReservation.class))).thenReturn(testReservation);

        // when
        Long reservationId = studyRoomReservationService.createReservation(testCreateDto, userDetails);

        // then
        assertThat(reservationId).isEqualTo(1L);
        verify(studyRoomRepository, times(2)).findByIdWithLock(1L);
        verify(studyRoomReservationRepository).save(any(StudyRoomReservation.class));
    }

    @Test
    @DisplayName("락 없는 예약 생성 성공")
    void createReservationWithNoLock_success() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomRepository.findById(1L)).thenReturn(Optional.of(testStudyRoom));
        when(studyRoomReservationRepository.findAllByStudyRoomAndEndTimeAfter(eq(testStudyRoom), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(studyRoomReservationRepository.save(any(StudyRoomReservation.class))).thenReturn(testReservation);

        // when
        Long reservationId = studyRoomReservationService.createReservationWithNoLock(testCreateDto, userDetails);

        // then
        assertThat(reservationId).isEqualTo(1L);
        
        ArgumentCaptor<StudyRoomReservation> captor = ArgumentCaptor.forClass(StudyRoomReservation.class);
        verify(studyRoomReservationRepository).save(captor.capture());
        StudyRoomReservation savedReservation = captor.getValue();
        
        assertThat(savedReservation.getUser()).isEqualTo(testUser);
        assertThat(savedReservation.getStudyRoom()).isEqualTo(testStudyRoom);
        assertThat(savedReservation.getReservationStart()).isEqualTo(futureStart);
        assertThat(savedReservation.getReservationEnd()).isEqualTo(futureEnd);
        assertThat(savedReservation.getPartyNum()).isEqualTo(2);
    }

    @Test
    @DisplayName("락 없는 예약 생성 실패 - 사용자를 찾을 수 없음")
    void createReservationWithNoLock_fail_userNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            studyRoomReservationService.createReservationWithNoLock(testCreateDto, userDetails);
        });
        
        verify(studyRoomRepository, never()).findById(any());
        verify(studyRoomReservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약 상세 조회 성공")
    void getReservationDetail_success() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testReservation));

        // when
        ReservationDetailDto result = studyRoomReservationService.getReservationDetail(1L, userDetails);

        // then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReservationStart()).isEqualTo(futureStart);
        assertThat(result.getReservationEnd()).isEqualTo(futureEnd);
        assertThat(result.getPartyNum()).isEqualTo(2);
        assertThat(result.getStudyRoomName()).isEqualTo("테스트 스터디룸");
        assertThat(result.getMaxNum()).isEqualTo(4);
        assertThat(result.getStudyHallName()).isEqualTo("테스트 스터디홀");
        assertThat(result.getSimpleAddress()).isEqualTo("서울시 강남구");
        assertThat(result.getAddress()).isEqualTo("서울시 강남구 테스트로 123");
    }

    @Test
    @DisplayName("예약 상세 조회 실패 - 사용자를 찾을 수 없음")
    void getReservationDetail_fail_userNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            studyRoomReservationService.getReservationDetail(1L, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).findByIdWithDetails(any());
    }

    @Test
    @DisplayName("예약 상세 조회 실패 - 예약을 찾을 수 없음")
    void getReservationDetail_fail_reservationNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyRoomReservationNotFoundException.class, () -> {
            studyRoomReservationService.getReservationDetail(1L, userDetails);
        });
    }

    @Test
    @DisplayName("예약 상세 조회 실패 - 권한 없음")
    void getReservationDetail_fail_unauthorized() {
        // given
        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(2L);
        when(otherUser.getUsername()).thenReturn("otheruser");
        
        StudyRoomReservation otherReservation = StudyRoomReservation.builder()
                .id(1L)
                .user(otherUser)
                .studyRoom(testStudyRoom)
                .reservationStart(futureStart)
                .reservationEnd(futureEnd)
                .partyNum(2)
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(otherReservation));

        // when & then
        assertThrows(UnauthorizedReservationAccessException.class, () -> {
            studyRoomReservationService.getReservationDetail(1L, userDetails);
        });
    }

    @Test
    @DisplayName("예약 목록 조회 성공")
    void getReservationList_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        
        List<StudyRoomReservation> reservationList = List.of(
                testReservation,
                StudyRoomReservation.builder()
                        .id(2L)
                        .user(testUser)
                        .studyRoom(testStudyRoom)
                        .reservationStart(futureStart.plusDays(1))
                        .reservationEnd(futureEnd.plusDays(1))
                        .partyNum(3)
                        .build()
        );
        
        Page<StudyRoomReservation> reservationPage = new PageImpl<>(reservationList, pageable, reservationList.size());
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findByUser(testUser, pageable)).thenReturn(reservationPage);

        // when
        Page<ReservationListDto> result = studyRoomReservationService.getReservationList(pageable, userDetails);

        // then
        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        
        ReservationListDto firstReservation = result.getContent().get(0);
        assertThat(firstReservation.getId()).isEqualTo(1L);
        assertThat(firstReservation.getReservationStart()).isEqualTo(futureStart);
        assertThat(firstReservation.getReservationEnd()).isEqualTo(futureEnd);
        assertThat(firstReservation.getPartyNum()).isEqualTo(2);
        assertThat(firstReservation.getStudyRoomName()).isEqualTo("테스트 스터디룸");
        assertThat(firstReservation.getSimpleAddress()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("예약 목록 조회 실패 - 사용자를 찾을 수 없음")
    void getReservationList_fail_userNotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            studyRoomReservationService.getReservationList(pageable, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).findByUser(any(), any());
    }

    @Test
    @DisplayName("예약 삭제 성공")
    void deleteReservation_success() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // when
        studyRoomReservationService.deleteReservation(1L, userDetails);

        // then
        verify(studyRoomReservationRepository).delete(testReservation);
    }

    @Test
    @DisplayName("예약 삭제 실패 - 사용자를 찾을 수 없음")
    void deleteReservation_fail_userNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            studyRoomReservationService.deleteReservation(1L, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).findById(any());
        verify(studyRoomReservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("예약 삭제 실패 - 예약을 찾을 수 없음")
    void deleteReservation_fail_reservationNotFound() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyRoomReservationNotFoundException.class, () -> {
            studyRoomReservationService.deleteReservation(1L, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("예약 삭제 실패 - 권한 없음")
    void deleteReservation_fail_unauthorized() {
        // given
        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn(2L);
        when(otherUser.getUsername()).thenReturn("otheruser");
        
        StudyRoomReservation otherReservation = StudyRoomReservation.builder()
                .id(1L)
                .user(otherUser)
                .studyRoom(testStudyRoom)
                .reservationStart(futureStart)
                .reservationEnd(futureEnd)
                .partyNum(2)
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findById(1L)).thenReturn(Optional.of(otherReservation));

        // when & then
        assertThrows(UnauthorizedReservationAccessException.class, () -> {
            studyRoomReservationService.deleteReservation(1L, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("예약 삭제 실패 - 이미 시작된 예약")
    void deleteReservation_fail_alreadyStarted() {
        // given
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        StudyRoomReservation startedReservation = StudyRoomReservation.builder()
                .id(1L)
                .user(testUser)
                .studyRoom(testStudyRoom)
                .reservationStart(pastTime)
                .reservationEnd(pastTime.plusHours(2))
                .partyNum(2)
                .build();
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(studyRoomReservationRepository.findById(1L)).thenReturn(Optional.of(startedReservation));

        // when & then
        assertThrows(ReservationAlreadyStartedException.class, () -> {
            studyRoomReservationService.deleteReservation(1L, userDetails);
        });
        
        verify(studyRoomReservationRepository, never()).delete(any());
    }
}