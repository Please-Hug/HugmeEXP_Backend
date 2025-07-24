package org.example.hugmeexp.domain.studyRoom.service;

import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.repository.StudyRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyRoomServiceTest {

    @InjectMocks
    private StudyRoomService studyRoomService;
    @Mock
    private StudyRoomRepository studyRoomRepository;
    @Mock
    private StudyHallRepository studyHallRepository;

    @Test
    @DisplayName("스터디 룸 생성 성공")
    void createStudyRoom_success() {

        // given
        Long studyHallId = 1L;
        StudyRoomRequest requestDto = new StudyRoomRequest("새로운 룸", 10);

        StudyHall parentHall = StudyHall.builder()
                .id(studyHallId)
                .name("강남 스터디 홀")
                .build();

        when(studyHallRepository.findById(studyHallId)).thenReturn(Optional.of(parentHall));

        // when
        studyRoomService.createStudyRoom(studyHallId, requestDto);

        // then
        ArgumentCaptor<StudyRoom> captor = ArgumentCaptor.forClass(StudyRoom.class);
        verify(studyRoomRepository).save(captor.capture());
        StudyRoom savedRoom = captor.getValue();

        assertNotNull(savedRoom);
        assertEquals("새로운 룸", savedRoom.getName());
        assertEquals(10, savedRoom.getMaxNum());
        assertEquals(parentHall, savedRoom.getStudyHall());
    }

    @Test
    @DisplayName("스터디 룸 생성 실패 - 부모 홀이 없을 경우")
    void createStudyRoom_fail_hallNotFound() {
        // given
        Long nonExistentHallId = 999L;
        StudyRoomRequest requestDto = new StudyRoomRequest();

        when(studyHallRepository.findById(nonExistentHallId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHallNotFoundException.class, () -> {
            studyRoomService.createStudyRoom(nonExistentHallId, requestDto);
        });
    }

    @Test
    @DisplayName("특정 홀의 룸 목록 조회 성공")
    void findAllRoomsInHall_success() {

        // given
        Long studyHallId = 1L;
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();
        List<StudyRoom> rooms = List.of(
                StudyRoom.builder().id(10L).build(),
                StudyRoom.builder().id(11L).build()
        );

        when(studyHallRepository.findById(studyHallId)).thenReturn(Optional.of(mockHall));
        when(studyRoomRepository.findAllByStudyHall(mockHall)).thenReturn(rooms);

        // when
        List<StudyRoom> results = studyRoomService.findAllRoomsInHall(studyHallId);

        // then
        assertThat(results).hasSize(2);
        verify(studyHallRepository, times(1)).findById(studyHallId);
        verify(studyRoomRepository, times(1)).findAllByStudyHall(mockHall);
    }

    @Test
    @DisplayName("특정 홀의 룸 목록 조회 실패 - 홀이 없을 경우")
    void findAllRoomsInHall_fail_hallNotFound() {

        // given
        Long nonExistentHallId = 999L;
        when(studyHallRepository.findById(nonExistentHallId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHallNotFoundException.class, () -> {
            studyRoomService.findAllRoomsInHall(nonExistentHallId);
        });

        verify(studyHallRepository, times(1)).findById(nonExistentHallId);
        verify(studyRoomRepository, never()).findAllByStudyHall(any());
    }
}