package org.example.hugmeexp.domain.studyRoom.service;

import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationListDto;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;
import org.example.hugmeexp.domain.studyRoom.exception.StudyRoomReservationNotFoundException;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomReservationRepository;
import org.example.hugmeexp.domain.studyRoom.service.AdminReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminReservationServiceTest {

    @InjectMocks
    private AdminReservationService adminReservationService;

    @Mock
    private StudyRoomReservationRepository studyRoomReservationRepository;

    @Test
    @DisplayName("전체 예약 목록 조회 성공")
    void getAllReservations_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        StudyHall hall = StudyHall.builder().name("Test Hall").location(Location.of(37.5665, 126.9780, "Test Address", "Test Simple Address")).build();
        StudyRoom room = StudyRoom.builder().name("Test Room").studyHall(hall).build();
        List<StudyRoomReservation> reservations = List.of(
                StudyRoomReservation.builder().id(1L).studyRoom(room).build()
        );
        Page<StudyRoomReservation> mockPage = new PageImpl<>(reservations, pageable, reservations.size());

        when(studyRoomReservationRepository.findAllWithDetails(pageable)).thenReturn(mockPage);

        // when
        Page<ReservationListDto> resultPage = adminReservationService.getAllReservations(pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent().get(0).getStudyRoomName()).isEqualTo("Test Room");
        verify(studyRoomReservationRepository).findAllWithDetails(pageable);
    }

    @Test
    @DisplayName("예약 강제 취소 성공")
    void forceCancelReservation_success() {
        // given
        Long reservationId = 1L;
        when(studyRoomReservationRepository.existsById(reservationId)).thenReturn(true);

        // when
        adminReservationService.forceCancelReservation(reservationId);

        // then
        verify(studyRoomReservationRepository, times(1)).existsById(reservationId);
        verify(studyRoomReservationRepository, times(1)).deleteById(reservationId);
    }

    @Test
    @DisplayName("예약 강제 취소 실패 - 존재하지 않는 예약")
    void forceCancelReservation_fail_notFound() {
        // given
        Long nonExistentId = 999L;
        when(studyRoomReservationRepository.existsById(nonExistentId)).thenReturn(false);

        // when & then
        assertThrows(StudyRoomReservationNotFoundException.class, () -> {
            adminReservationService.forceCancelReservation(nonExistentId);
        });

        verify(studyRoomReservationRepository, times(1)).existsById(nonExistentId);
        verify(studyRoomReservationRepository, never()).deleteById(anyLong());
    }
}