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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        StudyRoomRequest requestDto = new StudyRoomRequest();
        StudyHall parentHall = StudyHall.builder().id(studyHallId).build();
        StudyRoom newRoom = StudyRoom.builder().id(10L).name("새 룸").studyHall(parentHall).build();

        when(studyHallRepository.findById(studyHallId)).thenReturn(Optional.of(parentHall));
        when(studyRoomRepository.save(any(StudyRoom.class))).thenReturn(newRoom);

        // when
        StudyRoom result = studyRoomService.createStudyRoom(studyHallId, requestDto);

        // then
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("새 룸", result.getName());
        assertEquals(parentHall, result.getStudyHall()); // 부모 홀이 올바르게 연결되었는지 확인
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
        List<StudyRoom> rooms = List.of(
                StudyRoom.builder().id(10L).build(),
                StudyRoom.builder().id(11L).build()
        );

        when(studyHallRepository.existsById(studyHallId)).thenReturn(true);
        when(studyRoomRepository.findAllByStudyHallId(studyHallId)).thenReturn(rooms);

        // when
        List<StudyRoom> results = studyRoomService.findAllRoomsInHall(studyHallId);

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("특정 홀의 룸 목록 조회 실패 - 홀이 없을 경우")
    void findAllRoomsInHall_fail_hallNotFound() {
        // given
        Long nonExistentHallId = 999L;
        when(studyHallRepository.existsById(nonExistentHallId)).thenReturn(false);

        // when & then
        assertThrows(StudyHallNotFoundException.class, () -> {
            studyRoomService.findAllRoomsInHall(nonExistentHallId);
        });
    }
}