package org.example.hugmeexp.domain.studyRoom.service;

import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.exception.StudyHallNotFoundException;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyHallServiceTest {

    @InjectMocks
    private StudyHallService studyHallService;

    @Mock
    private StudyHallRepository studyHallRepository;

    @Test
    @DisplayName("스터디 홀 생성 성공")
    void createStudyHall_success() {
        // given
        StudyHallRequest requestDto = StudyHallRequest.builder()
                .name("테스트 홀")
                .description("테스트 설명입니다.")
                .simpleAddress("서울시 강남구")
                .build();

        // when
        studyHallService.createStudyHall(requestDto);

        // then
        ArgumentCaptor<StudyHall> captor = ArgumentCaptor.forClass(StudyHall.class);
        verify(studyHallRepository).save(captor.capture());
        StudyHall savedHall = captor.getValue();

        assertNotNull(savedHall);
        assertEquals("테스트 홀", savedHall.getName());
        assertEquals("테스트 설명입니다.", savedHall.getDescription());
        assertEquals("서울시 강남구", savedHall.getSimpleAddress());
    }

    @Test
    @DisplayName("전체 스터디 홀 조회 성공")
    void findAllStudyHalls_success() {
        // given
        StudyHall hall1 = StudyHall.builder().id(1L).build();
        StudyHall hall2 = StudyHall.builder().id(2L).build();
        List<StudyHall> studyHalls = List.of(hall1, hall2);

        when(studyHallRepository.findAll()).thenReturn(studyHalls);

        // when
        List<StudyHall> results = studyHallService.findAllStudyHalls();

        // then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("ID로 스터디 홀 조회 성공")
    void findStudyHallById_success() {
        // given
        Long studyHallId = 1L;
        StudyHall studyHall = StudyHall.builder().id(studyHallId).name("테스트 홀").build();

        when(studyHallRepository.findById(studyHallId)).thenReturn(Optional.of(studyHall));

        // when
        StudyHall result = studyHallService.findStudyHallById(studyHallId);

        // then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(studyHallId);
        assertThat(result.getName()).isEqualTo("테스트 홀");
    }

    @Test
    @DisplayName("ID로 스터디 홀 조회 실패 - 존재하지 않는 ID")
    void findStudyHallById_fail_notFound() {
        // given
        Long nonExistentId = 999L;

        when(studyHallRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        // findStudyHallById(999L)를 호출하면 StudyHallNotFoundException이 발생하는지 검증
        assertThrows(StudyHallNotFoundException.class, () -> {
            studyHallService.findStudyHallById(nonExistentId);
        });
    }
}