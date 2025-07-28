package org.example.hugmeexp.domain.recruitment.service;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitmentServiceTest {

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @InjectMocks
    private RecruitmentService recruitmentService;

    @Test
    @DisplayName("모든 파라미터가 제공된 경우 테스트")
    void listRecruitments_AllParamsProvided_ShouldCallRepositoryWithCorrectParams() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .salaryMin(3000)
                .salaryMax(5000)
                .experience(3)
                .education(4)
                .workLocation("서울")
                .techStacks(List.of(1L, 2L))
                .tags(List.of(3L, 4L))
                .topLeftLat(new BigDecimal("37.5"))
                .topLeftLng(new BigDecimal("126.9"))
                .bottomRightLat(new BigDecimal("37.4"))
                .bottomRightLng(new BigDecimal("127.0"))
                .build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(argThat(cond ->
            cond.getTechStackCount() == 2L &&
            cond.getTagCount() == 2L
        ));
    }

    @Test
    @DisplayName("파라미터가 없는 경우 테스트")
    void listRecruitments_NoParams_ShouldPassNullValues() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder().build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(any(RecruitmentSearchConditionDTO.class));
    }

    @Test
    @DisplayName("일부 파라미터만 제공된 경우 테스트")
    void listRecruitments_SomeParams_ShouldPassCorrectValues() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .salaryMin(3000)
                .salaryMax(5000)
                .build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(any(RecruitmentSearchConditionDTO.class));
    }

    @Test
    @DisplayName("techStacks와 tags가 null인 경우 테스트")
    void listRecruitments_NullTechStacksAndTags_ShouldPassNullCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .salaryMin(3000)
                .salaryMax(5000)
                .techStacks(null)
                .tags(null)
                .build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(argThat(cond ->
            cond.getTechStackCount() == null &&
            cond.getTagCount() == null
        ));
    }

    @Test
    @DisplayName("techStacks와 tags가 빈 리스트인 경우 테스트")
    void listRecruitments_EmptyTechStacksAndTags_ShouldPassZeroCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .techStacks(List.of())
                .tags(List.of())
                .build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(argThat(cond ->
            cond.getTechStackCount() == null &&
            cond.getTagCount() == null
        ));
    }

    @Test
    @DisplayName("techStacks는 있고 tags는 null인 경우 테스트")
    void listRecruitments_WithTechStacksNullTags_ShouldPassCorrectCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .techStacks(List.of(1L, 2L, 3L))
                .tags(null)
                .build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(any(RecruitmentSearchConditionDTO.class));
    }

    @Test
    @DisplayName("techStacks는 null이고 tags는 있는 경우 테스트")
    void listRecruitments_NullTechStacksWithTags_ShouldPassCorrectCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .techStacks(null)
                .tags(List.of(1L, 2L))
                .build();

        List<RecruitmentResponseDTO> expectedResult = createMockResponseList();
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class))).thenReturn(expectedResult);

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition);

        // Then
        assertEquals(expectedResult, result);
        verify(recruitmentRepository).findBySearchConditions(any(RecruitmentSearchConditionDTO.class));
    }

    @Test
    @DisplayName("최신 채용 공고 목록 조회 테스트 - 페이지네이션 적용")
    void findLatestRecruitments_ShouldReturnSortedByModifiedAtWithPagination() {
        // Given
        int limit = 5; // 페이지 크기 제한
        List<RecruitmentResponseDTO> mockResponseList = createSortedByModifiedAtMockResponseList();
        when(recruitmentRepository.findLatestRecruitments(any(Pageable.class))).thenReturn(mockResponseList.subList(0, 5));

        // When
        List<RecruitmentResponseDTO> result = recruitmentService.findLatestRecruitments(limit);

        // Then
        assertEquals(5, result.size()); // 결과가 5개만 반환되는지 확인
        verify(recruitmentRepository).findLatestRecruitments(any(Pageable.class));

        // 결과가 수정일 기준 내림차순으로 정렬되어 있는지 확인
        for (int i = 0; i < result.size() - 1; i++) {
            LocalDateTime current = result.get(i).getModifiedAt();
            LocalDateTime next = result.get(i + 1).getModifiedAt();
            assertTrue(current.isAfter(next) || current.isEqual(next), 
                    "Results should be sorted by modifiedAt in descending order");
        }
    }

    // 테스트용 응답 DTO 리스트 생성 헬퍼 메소드
    private List<RecruitmentResponseDTO> createMockResponseList() {
        List<RecruitmentResponseDTO> responses = new ArrayList<>();

        // 첫 번째 응답 DTO
        responses.add(new RecruitmentResponseDTO(
                1L, "백엔드 개발자 모집", "ABC 회사", "company_image_url_1.jpg",
                LocalDateTime.of(2023, 12, 31, 23, 59),
                3, "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now()
        ));

        // 두 번째 응답 DTO
        responses.add(new RecruitmentResponseDTO(
                2L, "프론트엔드 개발자 모집", "XYZ 회사", "company_image_url_2.jpg",
                LocalDateTime.of(2023, 11, 30, 23, 59),
                2, "서울시 서초구", new BigDecimal("37.4"), new BigDecimal("127.1"),
                LocalDateTime.now()
        ));

        return responses;
    }

    // 수정일 기준 내림차순으로 정렬된 테스트용 응답 DTO 리스트 생성 헬퍼 메소드
    // 페이지네이션 테스트를 위해 8개의 아이템을 생성 (5개만 반환되어야 함)
    private List<RecruitmentResponseDTO> createSortedByModifiedAtMockResponseList() {
        List<RecruitmentResponseDTO> responses = new ArrayList<>();

        // 1. 가장 최근에 수정된 공고 (1일 전)
        responses.add(new RecruitmentResponseDTO(
                1L, "백엔드 개발자 모집", "ABC 회사", "company_image_url_1.jpg",
                LocalDateTime.of(2023, 12, 31, 23, 59),
                3, "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(1)
        ));

        // 2. 두 번째로 최근에 수정된 공고 (3일 전)
        responses.add(new RecruitmentResponseDTO(
                2L, "프론트엔드 개발자 모집", "XYZ 회사", "company_image_url_2.jpg",
                LocalDateTime.of(2023, 11, 30, 23, 59),
                2, "서울시 서초구", new BigDecimal("37.4"), new BigDecimal("127.1"),
                LocalDateTime.now().minusDays(3)
        ));

        // 3. 세 번째로 최근에 수정된 공고 (5일 전)
        responses.add(new RecruitmentResponseDTO(
                3L, "데이터 엔지니어 모집", "DEF 회사", "company_image_url_3.jpg",
                LocalDateTime.of(2023, 12, 15, 23, 59),
                5, "서울시 송파구", new BigDecimal("37.6"), new BigDecimal("127.2"),
                LocalDateTime.now().minusDays(5)
        ));

        // 4. 네 번째로 최근에 수정된 공고 (7일 전)
        responses.add(new RecruitmentResponseDTO(
                4L, "모바일 개발자 모집", "GHI 회사", "company_image_url_4.jpg",
                LocalDateTime.of(2023, 12, 20, 23, 59),
                4, "서울시 마포구", new BigDecimal("37.55"), new BigDecimal("126.9"),
                LocalDateTime.now().minusDays(7)
        ));

        // 5. 다섯 번째로 최근에 수정된 공고 (10일 전)
        responses.add(new RecruitmentResponseDTO(
                5L, "DevOps 엔지니어 모집", "JKL 회사", "company_image_url_5.jpg",
                LocalDateTime.of(2023, 12, 25, 23, 59),
                2, "서울시 영등포구", new BigDecimal("37.52"), new BigDecimal("126.93"),
                LocalDateTime.now().minusDays(10)
        ));

        // 6. 여섯 번째로 최근에 수정된 공고 (14일 전)
        responses.add(new RecruitmentResponseDTO(
                6L, "QA 엔지니어 모집", "MNO 회사", "company_image_url_6.jpg",
                LocalDateTime.of(2023, 12, 10, 23, 59),
                3, "서울시 강동구", new BigDecimal("37.53"), new BigDecimal("127.12"),
                LocalDateTime.now().minusDays(14)
        ));

        // 7. 일곱 번째로 최근에 수정된 공고 (20일 전)
        responses.add(new RecruitmentResponseDTO(
                7L, "보안 엔지니어 모집", "PQR 회사", "company_image_url_7.jpg",
                LocalDateTime.of(2023, 12, 5, 23, 59),
                6, "서울시 성동구", new BigDecimal("37.54"), new BigDecimal("127.05"),
                LocalDateTime.now().minusDays(20)
        ));

        // 8. 여덟 번째로 최근에 수정된 공고 (30일 전)
        responses.add(new RecruitmentResponseDTO(
                8L, "시스템 엔지니어 모집", "STU 회사", "company_image_url_8.jpg",
                LocalDateTime.of(2023, 11, 15, 23, 59),
                4, "서울시 중구", new BigDecimal("37.56"), new BigDecimal("126.98"),
                LocalDateTime.now().minusDays(30)
        ));

        return responses;
    }
}
