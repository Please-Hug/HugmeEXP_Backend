package org.example.hugmeexp.domain.recruitment.service;

import org.example.hugmeexp.domain.recruitment.dto.*;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.entity.Tag;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.example.hugmeexp.domain.recruitment.entity.TechStack;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentNotFoundException;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitmentServiceTest {

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private TechItemRepository techItemRepository;

    @Mock
    private TagItemRepository tagItemRepository;

    @InjectMocks
    private RecruitmentService recruitmentService;

    @Test
    @DisplayName("모든 파라미터가 제공된 경우 테스트")
    void listRecruitments_AllParamsProvided_ShouldCallRepositoryWithCorrectParams() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .salaryMin(3000)
                .salaryMax(5000)
                .experienceMin(3)
                .experienceMax(7)
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
                .experienceMin(2)
                .experienceMax(5)
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
                .experienceMin(1)
                .experienceMax(10)
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
                3, 5, "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now()
        ));

        // 두 번째 응답 DTO
        responses.add(new RecruitmentResponseDTO(
                2L, "프론트엔드 개발자 모집", "XYZ 회사", "company_image_url_2.jpg",
                LocalDateTime.of(2023, 11, 30, 23, 59),
                2, 4, "서울시 서초구", new BigDecimal("37.4"), new BigDecimal("127.1"),
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
                3, 5, "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(1)
        ));

        // 2. 두 번째로 최근에 수정된 공고 (3일 전)
        responses.add(new RecruitmentResponseDTO(
                2L, "프론트엔드 개발자 모집", "XYZ 회사", "company_image_url_2.jpg",
                LocalDateTime.of(2023, 11, 30, 23, 59),
                2, 4, "서울시 서초구", new BigDecimal("37.4"), new BigDecimal("127.1"),
                LocalDateTime.now().minusDays(3)
        ));

        // 3. 세 번째로 최근에 수정된 공고 (5일 전)
        responses.add(new RecruitmentResponseDTO(
                3L, "데이터 엔지니어 모집", "DEF 회사", "company_image_url_3.jpg",
                LocalDateTime.of(2023, 12, 15, 23, 59),
                5, 7, "서울시 송파구", new BigDecimal("37.6"), new BigDecimal("127.2"),
                LocalDateTime.now().minusDays(5)
        ));

        // 4. 네 번째로 최근에 수정된 공고 (7일 전)
        responses.add(new RecruitmentResponseDTO(
                4L, "모바일 개발자 모집", "GHI 회사", "company_image_url_4.jpg",
                LocalDateTime.of(2023, 12, 20, 23, 59),
                4, 6, "서울시 마포구", new BigDecimal("37.55"), new BigDecimal("126.9"),
                LocalDateTime.now().minusDays(7)
        ));

        // 5. 다섯 번째로 최근에 수정된 공고 (10일 전)
        responses.add(new RecruitmentResponseDTO(
                5L, "DevOps 엔지니어 모집", "JKL 회사", "company_image_url_5.jpg",
                LocalDateTime.of(2023, 12, 25, 23, 59),
                2, 4, "서울시 영등포구", new BigDecimal("37.52"), new BigDecimal("126.93"),
                LocalDateTime.now().minusDays(10)
        ));

        // 6. 여섯 번째로 최근에 수정된 공고 (14일 전)
        responses.add(new RecruitmentResponseDTO(
                6L, "QA 엔지니어 모집", "MNO 회사", "company_image_url_6.jpg",
                LocalDateTime.of(2023, 12, 10, 23, 59),
                3, 5, "서울시 강동구", new BigDecimal("37.53"), new BigDecimal("127.12"),
                LocalDateTime.now().minusDays(14)
        ));

        // 7. 일곱 번째로 최근에 수정된 공고 (20일 전)
        responses.add(new RecruitmentResponseDTO(
                7L, "보안 엔지니어 모집", "PQR 회사", "company_image_url_7.jpg",
                LocalDateTime.of(2023, 12, 5, 23, 59),
                6, 8, "서울시 성동구", new BigDecimal("37.54"), new BigDecimal("127.05"),
                LocalDateTime.now().minusDays(20)
        ));

        // 8. 여덟 번째로 최근에 수정된 공고 (30일 전)
        responses.add(new RecruitmentResponseDTO(
                8L, "시스템 엔지니어 모집", "STU 회사", "company_image_url_8.jpg",
                LocalDateTime.of(2023, 11, 15, 23, 59),
                4, 6, "서울시 중구", new BigDecimal("37.56"), new BigDecimal("126.98"),
                LocalDateTime.now().minusDays(30)
        ));

        return responses;
    }

    @Test
    @DisplayName("채용 공고 상세 조회 성공 테스트")
    void getRecruitmentDetail_Success() {
        // Given
        Long recruitmentId = 1L;

        // 회사 정보 생성
        Company company = Company.builder()
                .id(1L)
                .companyName("ABC 회사")
                .companyAddress("서울시 강남구 테헤란로 123")
                .latitude(new BigDecimal("37.5"))
                .longitude(new BigDecimal("127.0"))
                .establishmentDate(LocalDate.of(2010, 1, 1))
                .companyImageUrl("company_image_url_1.jpg")
                .companyDescription("좋은 회사입니다.")
                .build();

        // 기술 스택 아이템 생성 (생성자 사용)
        TechItem techItem1 = new TechItem(1L, "Java", "자바", "java_icon.png");

        TechItem techItem2 = new TechItem(2L, "Spring", "스프링", "spring_icon.png");

        // 채용 공고 생성
        Recruitment recruitment = Recruitment.builder()
                .id(recruitmentId)
                .title("백엔드 개발자 모집")
                .education(4)
                .experienceMin(3)
                .experienceMax(5)
                .qualification("Java, Spring 경험자")
                .advantage("MSA 경험자 우대")
                .welfare("점심 제공, 4대 보험")
                .workLocation("서울시 강남구")
                .latitude(new BigDecimal("37.5"))
                .longitude(new BigDecimal("127.0"))
                .salaryMin(3000)
                .salaryMax(5000)
                .link("https://example.com/job/1")
                .dueDate(LocalDateTime.of(2023, 12, 31, 23, 59))
                .company(company)
                .build();

        // 기술 스택 생성 및 연결
        List<TechStack> techStacks = new ArrayList<>();
        TechStack techStack1 = TechStack.builder()
                .id(1L)
                .recruitment(recruitment)
                .techItem(techItem1)
                .build();

        TechStack techStack2 = TechStack.builder()
                .id(2L)
                .recruitment(recruitment)
                .techItem(techItem2)
                .build();

        techStacks.add(techStack1);
        techStacks.add(techStack2);

        // 태그 아이템 생성
        TagItem tagItem1 = new TagItem(1L, "신입");
        TagItem tagItem2 = new TagItem(2L, "경력");

        // 태그 생성 및 연결
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = Tag.builder()
                .id(1L)
                .recruitment(recruitment)
                .tagItem(tagItem1)
                .build();

        Tag tag2 = Tag.builder()
                .id(2L)
                .recruitment(recruitment)
                .tagItem(tagItem2)
                .build();

        tags.add(tag1);
        tags.add(tag2);

        // 채용 공고에 기술 스택과 태그 설정
        recruitment = recruitment.toBuilder()
                .techStacks(techStacks)
                .tags(tags)
                .build();

        // Repository mock 설정
        when(recruitmentRepository.findDetailById(recruitmentId)).thenReturn(Optional.of(recruitment));

        // When
        RecruitmentDetailResponseDTO result = recruitmentService.getRecruitmentDetail(recruitmentId);

        // Then
        assertNotNull(result);
        assertEquals(recruitmentId, result.getId());
        assertEquals("백엔드 개발자 모집", result.getTitle());
        assertEquals("ABC 회사", result.getCompanyName());
        assertEquals("company_image_url_1.jpg", result.getCompanyImageUrl());
        assertEquals("서울시 강남구 테헤란로 123", result.getCompanyAddress());
        assertEquals(LocalDate.of(2010, 1, 1), result.getEstablishmentDate());
        assertEquals("좋은 회사입니다.", result.getCompanyDescription());
        assertEquals(LocalDateTime.of(2023, 12, 31, 23, 59), result.getDueDate());
        assertEquals(3, result.getExperienceMin());
        assertEquals(5, result.getExperienceMax());
        assertEquals(4, result.getEducation());
        assertEquals(3000, result.getSalaryMin());
        assertEquals(5000, result.getSalaryMax());
        assertEquals("Java, Spring 경험자", result.getQualifications());
        assertEquals("MSA 경험자 우대", result.getAdvantage());
        assertEquals("점심 제공, 4대 보험", result.getWelfare());
        assertEquals("https://example.com/job/1", result.getLink());

        // 기술 스택 검증
        assertEquals(2, result.getTechStacks().size());

        TechStackDTO techStackDTO1 = result.getTechStacks().get(0);
        assertEquals("자바", techStackDTO1.getLabelKo());
        assertEquals("Java", techStackDTO1.getLabelEn());
        assertEquals("java_icon.png", techStackDTO1.getIconUrl());

        TechStackDTO techStackDTO2 = result.getTechStacks().get(1);
        assertEquals("스프링", techStackDTO2.getLabelKo());
        assertEquals("Spring", techStackDTO2.getLabelEn());
        assertEquals("spring_icon.png", techStackDTO2.getIconUrl());

        // Repository 호출 검증
        verify(recruitmentRepository).findDetailById(recruitmentId);
    }

    @Test
    @DisplayName("존재하지 않는 채용 공고 상세 조회 테스트")
    void getRecruitmentDetail_NotFound() {
        // Given
        Long nonExistentId = 999L;
        when(recruitmentRepository.findDetailById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecruitmentNotFoundException.class, () -> {
            recruitmentService.getRecruitmentDetail(nonExistentId);
        });

        // Repository 호출 검증
        verify(recruitmentRepository).findDetailById(nonExistentId);
    }
    @Test
    @DisplayName("키워드로 채용 공고 검색 - 제목에 키워드가 포함된 경우")
    void findByKeyword_KeywordInTitle_ShouldReturnMatchingRecruitments() {
        // Given
        String keyword = "백엔드";
        int limit = 10;

        // 회사 정보 생성
        Company company1 = Company.builder()
                .id(1L)
                .companyName("ABC 회사")
                .companyAddress("서울시 강남구 테헤란로 123")
                .latitude(new BigDecimal("37.5"))
                .longitude(new BigDecimal("127.0"))
                .establishmentDate(LocalDate.of(2010, 1, 1))
                .companyImageUrl("company_image_url_1.jpg")
                .companyDescription("좋은 회사입니다.")
                .build();

        // 채용 공고 생성 (키워드가 제목에 포함됨)
        Recruitment recruitment1 = Recruitment.builder()
                .id(1L)
                .title("백엔드 개발자 모집")
                .education(4)
                .experienceMin(3)
                .experienceMax(5)
                .workLocation("서울시 강남구")
                .company(company1)
                .build();

        List<Recruitment> mockRecruitments = List.of(recruitment1);

        // Repository mock 설정
        when(recruitmentRepository.findByKeyword(keyword, PageRequest.of(0, limit))).thenReturn(mockRecruitments);

        // When
        List<RecruitmentCompanySearchResponseDTO> result = recruitmentService.findByKeyword(keyword, limit);

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRecruitmentId());
        assertEquals("백엔드 개발자 모집", result.get(0).getTitle());
        assertEquals(1L, result.get(0).getCompanyId());
        assertEquals("ABC 회사", result.get(0).getCompanyName());

        // Repository 호출 검증
        verify(recruitmentRepository).findByKeyword(keyword, PageRequest.of(0, limit));
    }

    @Test
    @DisplayName("키워드로 채용 공고 검색 - 회사명에 키워드가 포함된 경우")
    void findByKeyword_KeywordInCompanyName_ShouldReturnMatchingRecruitments() {
        // Given
        String keyword = "XYZ";
        int limit = 10;

        // 회사 정보 생성 (키워드가 회사명에 포함됨)
        Company company1 = Company.builder()
                .id(2L)
                .companyName("XYZ 회사")
                .companyAddress("서울시 서초구 서초대로 456")
                .latitude(new BigDecimal("37.4"))
                .longitude(new BigDecimal("127.1"))
                .establishmentDate(LocalDate.of(2015, 5, 5))
                .companyImageUrl("company_image_url_2.jpg")
                .companyDescription("혁신적인 회사입니다.")
                .build();

        // 채용 공고 생성
        Recruitment recruitment1 = Recruitment.builder()
                .id(2L)
                .title("프론트엔드 개발자 모집")
                .education(4)
                .experienceMin(2)
                .experienceMax(4)
                .workLocation("서울시 서초구")
                .company(company1)
                .build();

        List<Recruitment> mockRecruitments = List.of(recruitment1);

        // Repository mock 설정
        when(recruitmentRepository.findByKeyword(keyword, PageRequest.of(0, limit))).thenReturn(mockRecruitments);

        // When
        List<RecruitmentCompanySearchResponseDTO> result = recruitmentService.findByKeyword(keyword, limit);

        // Then
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getRecruitmentId());
        assertEquals("프론트엔드 개발자 모집", result.get(0).getTitle());
        assertEquals(2L, result.get(0).getCompanyId());
        assertEquals("XYZ 회사", result.get(0).getCompanyName());

        // Repository 호출 검증
        verify(recruitmentRepository).findByKeyword(keyword, PageRequest.of(0, limit));
    }

    @Test
    @DisplayName("키워드로 채용 공고 검색 - 일치하는 결과가 없는 경우")
    void findByKeyword_NoMatches_ShouldReturnEmptyList() {
        // Given
        String keyword = "존재하지않는키워드";
        int limit = 10;

        // Repository mock 설정 - 빈 리스트 반환
        when(recruitmentRepository.findByKeyword(keyword, PageRequest.of(0, limit))).thenReturn(List.of());

        // When
        List<RecruitmentCompanySearchResponseDTO> result = recruitmentService.findByKeyword(keyword, limit);

        // Then
        assertTrue(result.isEmpty());

        // Repository 호출 검증
        verify(recruitmentRepository).findByKeyword(keyword, PageRequest.of(0, limit));
    }

    @Test
    @DisplayName("키워드로 채용 공고 검색 - 결과 제한 테스트")
    void findByKeyword_LimitResults_ShouldReturnLimitedResults() {
        // Given
        String keyword = "개발자";
        int limit = 2;

        // 회사 정보 생성
        Company company1 = Company.builder()
                .id(1L)
                .companyName("ABC 회사")
                .companyAddress("서울시 강남구 테헤란로 123")
                .latitude(new BigDecimal("37.5"))
                .longitude(new BigDecimal("127.0"))
                .establishmentDate(LocalDate.of(2010, 1, 1))
                .companyImageUrl("company_image_url_1.jpg")
                .companyDescription("좋은 회사입니다.")
                .build();

        Company company2 = Company.builder()
                .id(2L)
                .companyName("XYZ 회사")
                .companyAddress("서울시 서초구 서초대로 456")
                .latitude(new BigDecimal("37.4"))
                .longitude(new BigDecimal("127.1"))
                .establishmentDate(LocalDate.of(2015, 5, 5))
                .companyImageUrl("company_image_url_2.jpg")
                .companyDescription("혁신적인 회사입니다.")
                .build();

        Company company3 = Company.builder()
                .id(3L)
                .companyName("DEF 회사")
                .companyAddress("서울시 송파구 올림픽로 789")
                .latitude(new BigDecimal("37.6"))
                .longitude(new BigDecimal("127.2"))
                .establishmentDate(LocalDate.of(2012, 3, 3))
                .companyImageUrl("company_image_url_3.jpg")
                .companyDescription("성장하는 회사입니다.")
                .build();

        // 채용 공고 생성
        Recruitment recruitment1 = Recruitment.builder()
                .id(1L)
                .title("백엔드 개발자 모집")
                .education(4)
                .experienceMin(3)
                .experienceMax(5)
                .workLocation("서울시 강남구")
                .company(company1)
                .build();

        Recruitment recruitment2 = Recruitment.builder()
                .id(2L)
                .title("프론트엔드 개발자 모집")
                .education(4)
                .experienceMin(2)
                .experienceMax(4)
                .workLocation("서울시 서초구")
                .company(company2)
                .build();

        Recruitment recruitment3 = Recruitment.builder()
                .id(3L)
                .title("모바일 개발자 모집")
                .education(4)
                .experienceMin(5)
                .experienceMax(7)
                .workLocation("서울시 송파구")
                .company(company3)
                .build();

        // 3개의 결과가 있지만 limit이 2이므로 2개만 반환되어야 함
        List<Recruitment> mockRecruitments = List.of(recruitment1, recruitment2);

        // Repository mock 설정
        when(recruitmentRepository.findByKeyword(keyword, PageRequest.of(0, limit))).thenReturn(mockRecruitments);

        // When
        List<RecruitmentCompanySearchResponseDTO> result = recruitmentService.findByKeyword(keyword, limit);

        // Then
        assertEquals(2, result.size());

        // Repository 호출 검증
        verify(recruitmentRepository).findByKeyword(keyword, PageRequest.of(0, limit));
    }

    @Test
    @DisplayName("채용 공고 필터 옵션 조회 테스트")
    void getFilterOptions_ShouldReturnAllFilterOptions() {
        // Given
        // TechItem 목 데이터 생성
        List<TechItem> techItems = List.of(
                new TechItem(1L, "Java", "자바", "java_icon.png"),
                new TechItem(2L, "Spring", "스프링", "spring_icon.png"),
                new TechItem(3L, "Python", "파이썬", "python_icon.png")
        );

        // TagItem 목 데이터 생성
        List<TagItem> tagItems = List.of(
                new TagItem(1L, "신입"),
                new TagItem(2L, "경력"),
                new TagItem(3L, "재택근무")
        );

        // Repository mock 설정
        when(techItemRepository.findAll()).thenReturn(techItems);
        when(tagItemRepository.findAll()).thenReturn(tagItems);

        // When
        RecruitmentFilterResponseDTO result = recruitmentService.getFilterOptions();

        // Then
        // 교육 옵션 검증
        assertEquals(6, result.getEducationOptions().size());
        assertEquals("무관", result.getEducationOptions().get(0).getLabel());
        assertEquals(0, result.getEducationOptions().get(0).getValue());
        assertEquals("고졸", result.getEducationOptions().get(1).getLabel());
        assertEquals(10, result.getEducationOptions().get(1).getValue());
        assertEquals("초대졸", result.getEducationOptions().get(2).getLabel());
        assertEquals(20, result.getEducationOptions().get(2).getValue());
        assertEquals("대졸", result.getEducationOptions().get(3).getLabel());
        assertEquals(30, result.getEducationOptions().get(3).getValue());
        assertEquals("석사", result.getEducationOptions().get(4).getLabel());
        assertEquals(40, result.getEducationOptions().get(4).getValue());
        assertEquals("박사", result.getEducationOptions().get(5).getLabel());
        assertEquals(50, result.getEducationOptions().get(5).getValue());

        // 경력 옵션 검증
        assertEquals(11, result.getExperienceOptions().size());
        for (int i = 0; i <= 10; i++) {
            assertEquals(i, result.getExperienceOptions().get(i));
        }

        // 기술 스택 검증
        assertEquals(3, result.getTechStacks().size());

        TechStackDTO techStack1 = result.getTechStacks().get(0);
        assertEquals(1L, techStack1.getId());
        assertEquals("자바", techStack1.getLabelKo());
        assertEquals("Java", techStack1.getLabelEn());
        assertEquals("java_icon.png", techStack1.getIconUrl());

        TechStackDTO techStack2 = result.getTechStacks().get(1);
        assertEquals(2L, techStack2.getId());
        assertEquals("스프링", techStack2.getLabelKo());
        assertEquals("Spring", techStack2.getLabelEn());
        assertEquals("spring_icon.png", techStack2.getIconUrl());

        TechStackDTO techStack3 = result.getTechStacks().get(2);
        assertEquals(3L, techStack3.getId());
        assertEquals("파이썬", techStack3.getLabelKo());
        assertEquals("Python", techStack3.getLabelEn());
        assertEquals("python_icon.png", techStack3.getIconUrl());

        // 근무 지역 검증
        assertEquals(3, result.getWorkLocations().size());
        assertEquals("판교", result.getWorkLocations().get(0));
        assertEquals("강남", result.getWorkLocations().get(1));
        assertEquals("구로", result.getWorkLocations().get(2));

        // 태그 검증
        assertEquals(3, result.getTags().size());

        TagDTO tag1 = result.getTags().get(0);
        assertEquals(1L, tag1.getId());
        assertEquals("신입", tag1.getTagName());

        TagDTO tag2 = result.getTags().get(1);
        assertEquals(2L, tag2.getId());
        assertEquals("경력", tag2.getTagName());

        TagDTO tag3 = result.getTags().get(2);
        assertEquals(3L, tag3.getId());
        assertEquals("재택근무", tag3.getTagName());

        // 급여 범위 검증
        assertEquals(0, result.getSalaryRange().getMin());
        assertEquals(10000, result.getSalaryRange().getMax());

        // Repository 호출 검증
        verify(techItemRepository).findAll();
        verify(tagItemRepository).findAll();
    }
}
