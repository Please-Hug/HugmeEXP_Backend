package org.example.hugmeexp.domain.studyRoom.service;

import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.exception.StudyRoomNotFoundException;
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
        StudyRoomRequest requestDto = new StudyRoomRequest("새 룸", 4);
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();

        when(studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)).thenReturn(Optional.of(mockHall));

        // when
        studyRoomService.createStudyRoom(studyHallId, requestDto);

        // then
        verify(studyRoomRepository).save(any(StudyRoom.class));
    }

    @Test
    @DisplayName("스터디 룸 생성 실패 - 부모 홀 없음")
    void createStudyRoom_fail_hallNotFound() {
        // given
        Long nonExistentHallId = 999L;
        StudyRoomRequest requestDto = new StudyRoomRequest("새 룸", 4);

        when(studyHallRepository.findByIdAndIsDeletedFalse(nonExistentHallId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHallNotFoundException.class, () -> {
            studyRoomService.createStudyRoom(nonExistentHallId, requestDto);
        });

        verify(studyRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("특정 홀의 룸 목록 조회 성공")
    void findAllRoomsInHall_success() {
        // given
        Long studyHallId = 1L;
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();
        List<StudyRoom> rooms = List.of(StudyRoom.builder().build());

        when(studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)).thenReturn(Optional.of(mockHall));
        when(studyRoomRepository.findAllByStudyHall(mockHall)).thenReturn(rooms);

        // when
        List<StudyRoom> results = studyRoomService.findAllRoomsInHall(studyHallId);

        // then
        assertThat(results).isEqualTo(rooms);
    }


    @Test
    @DisplayName("특정 홀의 룸 목록 조회 실패 - 홀이 없을 경우")
    void findAllRoomsInHall_fail_hallNotFound() {
        // given
        Long nonExistentHallId = 999L;

        when(studyHallRepository.findByIdAndIsDeletedFalse(nonExistentHallId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyHallNotFoundException.class, () -> {
            studyRoomService.findAllRoomsInHall(nonExistentHallId);
        });
    }

    @Test
    @DisplayName("스터디 룸 정보 수정 성공")
    void updateStudyRoom_success() {
        // given
        Long studyHallId = 1L;
        Long roomId = 10L;
        StudyRoomRequest requestDto = new StudyRoomRequest("수정된 룸 이름", 5);
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();
        StudyRoom originalRoom = StudyRoom.builder().id(roomId).name("기존 룸 이름").maxNum(4).studyHall(mockHall).build();

        when(studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)).thenReturn(Optional.of(mockHall));
        when(studyRoomRepository.findById(roomId)).thenReturn(Optional.of(originalRoom));

        // when
        studyRoomService.updateStudyRoom(studyHallId, roomId, requestDto);

        // then
        assertThat(originalRoom.getName()).isEqualTo("수정된 룸 이름");
        assertThat(originalRoom.getMaxNum()).isEqualTo(5);
        verify(studyHallRepository).findByIdAndIsDeletedFalse(studyHallId);
        verify(studyRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("스터디 룸 정보 수정 실패 - 룸을 찾을 수 없음")
    void updateStudyRoom_fail_roomNotFound() {
        // given
        Long studyHallId = 1L;
        Long nonExistentRoomId = 999L;
        StudyRoomRequest requestDto = new StudyRoomRequest("수정된 룸 이름", 5);
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();

        when(studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)).thenReturn(Optional.of(mockHall));
        when(studyRoomRepository.findById(nonExistentRoomId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StudyRoomNotFoundException.class, () -> {
            studyRoomService.updateStudyRoom(studyHallId, nonExistentRoomId, requestDto);
        });
    }

    @Test
    @DisplayName("스터디 룸 삭제 성공")
    void deleteStudyRoom_success() {
        // given
        Long studyHallId = 1L;
        Long roomId = 10L;
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();

        when(studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)).thenReturn(Optional.of(mockHall));
        when(studyRoomRepository.existsById(roomId)).thenReturn(true);

        // when
        studyRoomService.deleteStudyRoom(studyHallId, roomId);

        // then
        verify(studyRoomRepository, times(1)).deleteById(roomId);
    }

    @Test
    @DisplayName("스터디 룸 삭제 실패 - 룸을 찾을 수 없음")
    void deleteStudyRoom_fail_roomNotFound() {
        // given
        Long studyHallId = 1L;
        Long nonExistentRoomId = 999L;
        StudyHall mockHall = StudyHall.builder().id(studyHallId).build();

        when(studyHallRepository.findByIdAndIsDeletedFalse(studyHallId)).thenReturn(Optional.of(mockHall));
        when(studyRoomRepository.existsById(nonExistentRoomId)).thenReturn(false);

        // when & then
        assertThrows(StudyRoomNotFoundException.class, () -> {
            studyRoomService.deleteStudyRoom(studyHallId, nonExistentRoomId);
        });
        verify(studyRoomRepository, never()).deleteById(any());
    }
}