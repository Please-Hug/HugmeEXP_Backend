package org.example.hugmeexp.domain.recruitment.service;

import org.example.hugmeexp.domain.recruitment.dto.*;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.entity.Tag;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.example.hugmeexp.domain.recruitment.entity.TechStack;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentNotFoundException;
import org.example.hugmeexp.domain.recruitment.dto.TechItemRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.TagRequestDTO;
import org.example.hugmeexp.domain.recruitment.enums.SourceType;
import org.example.hugmeexp.domain.recruitment.repository.CompanyRepository;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TagRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechItemRepository;
import org.example.hugmeexp.domain.recruitment.repository.TechStackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitmentServiceTest {

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private TechItemRepository techItemRepository;

    @Mock
    private TechStackRepository techStackRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagItemRepository tagItemRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private RecruitmentService recruitmentService;

    private RecruitmentRequestDTO recruitmentRequestDTO;
    private Company company;
    private Recruitment recruitment;
    private TechItem techItem1, techItem2;
    private TagItem tagItem1, tagItem2;

    @BeforeEach
    void setUp() {
    // 테스트용 CompanyRequestDTO 생성
    CompanyRequestDTO companyRequestDTO = CompanyRequestDTO.builder()
            .companyName("테스트 회사")
            .companyAddress("서울시 강남구 테헤란로 123")
            .establishmentDate(LocalDateTime.now().toLocalDate())
            .companyDescription("테스트 회사 설명")
            .companyImageUrl("https://test.com")
            .latitude(BigDecimal.valueOf(123.456))
            .longitude(BigDecimal.valueOf(78.910))
            .build();

        // 테스트용 TechItemRequestDTO 생성
        List<TechItemRequestDTO> techItems = List.of(
                new TechItemRequestDTO("Java", "자바", ""),
                new TechItemRequestDTO("Spring", "스프링", "")
        );

        // 테스트용 TagRequestDTO 생성
        List<TagRequestDTO> tags = List.of(
                new TagRequestDTO("신입"),
                new TagRequestDTO("정규직")
        );

        // 테스트용 RecruitmentRequestDTO 생성
        recruitmentRequestDTO = new RecruitmentRequestDTO();
        recruitmentRequestDTO.setRecruitmentSourceId("TEST_001");
        recruitmentRequestDTO.setTitle("백엔드 개발자 모집");
        recruitmentRequestDTO.setEducation(4);
        recruitmentRequestDTO.setExperienceMin(2);
        recruitmentRequestDTO.setExperienceMax(2);
        recruitmentRequestDTO.setQualification("컴퓨터 관련 전공");
        recruitmentRequestDTO.setAdvantage("Spring 프레임워크 경험");
        recruitmentRequestDTO.setWelfare("4대 보험, 점심 제공");
        recruitmentRequestDTO.setWorkLocation("서울시 강남구");
        recruitmentRequestDTO.setLatitude(new BigDecimal("37.5665"));
        recruitmentRequestDTO.setLongitude(new BigDecimal("126.9780"));
        recruitmentRequestDTO.setSalaryMin(3000);
        recruitmentRequestDTO.setSalaryMax(5000);
        recruitmentRequestDTO.setLink("https://test-company.com/jobs/1");
        recruitmentRequestDTO.setSource(SourceType.WANTED);
        recruitmentRequestDTO.setDueDate(LocalDateTime.now().plusDays(30));
        recruitmentRequestDTO.setCompany(companyRequestDTO);
        recruitmentRequestDTO.setRequiredSkills(techItems);
        recruitmentRequestDTO.setTags(tags);

        // 테스트용 Company 엔티티
        company = Company.builder()
                .id(1L)
                .companyName("테스트 회사")
                .companyAddress("서울시 강남구 테헤란로 123")
                .establishmentDate(LocalDateTime.now().toLocalDate())
                .companyDescription("테스트 회사 설명")
                .companyImageUrl("https://test.com")
                .latitude(BigDecimal.valueOf(123.456))
                .longitude(BigDecimal.valueOf(78.910))
                .build();

        // 테스트용 Recruitment 엔티티
        recruitment = Recruitment.builder()
                .id(1L)
                .recruitmentSourceId("TEST_001")
                .title("백엔드 개발자 모집")
                .education(4)
                .experienceMin(2)
                .experienceMax(2)
                .qualification("컴퓨터 관련 전공")
                .advantage("Spring 프레임워크 경험")
                .welfare("4대 보험, 점심 제공")
                .workLocation("서울시 강남구")
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .salaryMin(3000)
                .salaryMax(5000)
                .link("https://test-company.com/jobs/1")
                .source(SourceType.WANTED)
                .dueDate(LocalDateTime.now().plusDays(30))
                .company(company)
                .techStacks(new ArrayList<>())
                .tags(new HashSet<>())
                .build();

        // 테스트용 TechItem 엔티티들
        techItem1 = TechItem.builder()
                .id(1L)
                .englishName("Java")
                .koreanName("자바")
                .iconUrl("")
                .build();

        techItem2 = TechItem.builder()
                .id(2L)
                .englishName("Spring")
                .koreanName("스프링")
                .iconUrl("")
                .build();

        // 테스트용 TagItem 엔티티들
        tagItem1 = TagItem.builder()
                .id(1L)
                .tagName("신입")
                .build();

        tagItem2 = TagItem.builder()
                .id(2L)
                .tagName("정규직")
                .build();
    }

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

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> cond.getTechStackCount() == 2L && cond.getTagCount() == 2L),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("파라미터가 없는 경우 테스트")
    void listRecruitments_NoParams_ShouldPassNullValues() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder().build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            any(RecruitmentSearchConditionDTO.class),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
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

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            any(RecruitmentSearchConditionDTO.class),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
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

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> cond.getTechStackCount() == null && cond.getTagCount() == null),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("techStacks와 tags가 빈 리스트인 경우 테스트")
    void listRecruitments_EmptyTechStacksAndTags_ShouldPassZeroCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .techStacks(List.of())
                .tags(List.of())
                .build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> cond.getTechStackCount() == null && cond.getTagCount() == null),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("techStacks는 있고 tags는 null인 경우 테스트")
    void listRecruitments_WithTechStacksNullTags_ShouldPassCorrectCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .techStacks(List.of(1L, 2L, 3L))
                .tags(null)
                .build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> cond.getTechStackCount() == 3L && cond.getTagCount() == null),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("techStacks는 null이고 tags는 있는 경우 테스트")
    void listRecruitments_NullTechStacksWithTags_ShouldPassCorrectCounts() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .techStacks(null)
                .tags(List.of(1L, 2L))
                .build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> cond.getTechStackCount() == null && cond.getTagCount() == 2L),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("채용 공고 목록 조회 - 페이지네이션 테스트 (40개 항목)")
    void listRecruitments_ShouldReturnPageWith40Items() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder().build();
        int page = 0;

        // 50개의 아이템을 생성 (40개만 반환되어야 함)
        List<RecruitmentResponseDTO> mockItems = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            mockItems.add(new RecruitmentResponseDTO(
                (long) i, "test::" + i, "개발자 모집 " + i, "회사 " + i, "image_" + i + ".jpg",
                LocalDateTime.now().plusDays(30), // 만료되지 않은 공고
                2, 5, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(i) // 최신순으로 정렬되도록 설정
            ));
        }

        // 첫 페이지에는 40개의 아이템만 포함되어야 함
        Page<RecruitmentResponseDTO> mockPage = new PageImpl<>(
            mockItems.subList(0, 40), 
            PageRequest.of(page, 40), 
            mockItems.size()
        );

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(mockPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(40, result.getContent().size()); // 페이지당 40개 항목
        assertEquals(50, result.getTotalElements()); // 전체 50개 항목
        assertEquals(2, result.getTotalPages()); // 총 2페이지 (40 + 10)
        verify(recruitmentRepository).findBySearchConditions(
            any(RecruitmentSearchConditionDTO.class),
            argThat(pageable -> pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("채용 공고 목록 조회 - 최신순 정렬 테스트")
    void listRecruitments_ShouldReturnSortedByModifiedAtDesc() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder().build();
        int page = 0;

        // 여러 날짜의 수정일을 가진 아이템 생성
        List<RecruitmentResponseDTO> mockItems = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mockItems.add(new RecruitmentResponseDTO(
                (long) i, "test::" + i, "개발자 모집 " + i, "회사 " + i, "image_" + i + ".jpg",
                LocalDateTime.now().plusDays(30),
                2, 5, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(i) // 최신순으로 정렬되도록 설정
            ));
        }

        Page<RecruitmentResponseDTO> mockPage = createMockResponsePage(mockItems, page, 40);
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(mockPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        List<RecruitmentResponseDTO> content = result.getContent();

        // 결과가 수정일 기준 내림차순으로 정렬되어 있는지 확인
        for (int i = 0; i < content.size() - 1; i++) {
            LocalDateTime current = content.get(i).getModifiedAt();
            LocalDateTime next = content.get(i + 1).getModifiedAt();
            assertTrue(current.isAfter(next) || current.isEqual(next), 
                    "결과는 수정일 기준 내림차순으로 정렬되어야 합니다");
        }
    }

    @Test
    @DisplayName("채용 공고 목록 조회 - 만료된 공고 제외 테스트")
    void listRecruitments_ShouldExcludeExpiredRecruitments() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder().build();
        int page = 0;

        // 만료된 공고와 유효한 공고를 모두 포함하는 목록 생성
        List<RecruitmentResponseDTO> allItems = new ArrayList<>();

        // 만료된 공고 (dueDate가 현재보다 이전)
        for (int i = 1; i <= 5; i++) {
            allItems.add(new RecruitmentResponseDTO(
                (long) i, "expired::" + i, "만료된 공고 " + i, "회사 " + i, "image_" + i + ".jpg",
                LocalDateTime.now().minusDays(i), // 만료된 공고
                2, 5, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(30)
            ));
        }

        // 유효한 공고 (dueDate가 현재보다 이후)
        List<RecruitmentResponseDTO> validItems = new ArrayList<>();
        for (int i = 6; i <= 10; i++) {
            RecruitmentResponseDTO validItem = new RecruitmentResponseDTO(
                (long) i, "valid::" + i, "유효한 공고 " + i, "회사 " + i, "image_" + i + ".jpg",
                LocalDateTime.now().plusDays(i), // 유효한 공고
                2, 5, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(i)
            );
            allItems.add(validItem);
            validItems.add(validItem);
        }

        // 레포지토리는 이미 만료된 공고를 필터링한 결과를 반환해야 함
        Page<RecruitmentResponseDTO> mockPage = createMockResponsePage(validItems, page, 40);
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(mockPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(5, result.getContent().size()); // 유효한 공고만 5개

        // 모든 공고의 dueDate가 현재 시간 이후인지 확인
        LocalDateTime now = LocalDateTime.now();
        for (RecruitmentResponseDTO item : result.getContent()) {
            assertTrue(item.getDueDate().isAfter(now), 
                    "모든 공고는 현재 시간 이후의 dueDate를 가져야 합니다");
            assertTrue(item.getRecruitmentSourceId().startsWith("valid::"), 
                    "모든 공고는 유효한 공고여야 합니다");
        }
    }

    @Test
    @DisplayName("채용 공고 목록 조회 - 경력 범위 필터링 테스트")
    void listRecruitments_ShouldFilterByExperienceRange() {
        // Given
        // 경력 범위 3-5년으로 검색 조건 설정
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .experienceMin(3)
                .experienceMax(5)
                .build();
        int page = 0;

        // 다양한 경력 범위를 가진 공고 생성
        List<RecruitmentResponseDTO> allItems = new ArrayList<>();

        // 1. 경력 범위가 1-2년 (조건에 맞지 않음)
        allItems.add(new RecruitmentResponseDTO(
            1L, "exp::1", "경력 1-2년 공고", "회사 1", "image_1.jpg",
            LocalDateTime.now().plusDays(30),
            1, 2, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
            LocalDateTime.now().minusDays(1)
        ));

        // 2. 경력 범위가 2-4년 (조건과 겹침 - 포함되어야 함)
        RecruitmentResponseDTO overlapping1 = new RecruitmentResponseDTO(
            2L, "exp::2", "경력 2-4년 공고", "회사 2", "image_2.jpg",
            LocalDateTime.now().plusDays(30),
            2, 4, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
            LocalDateTime.now().minusDays(2)
        );
        allItems.add(overlapping1);

        // 3. 경력 범위가 3-5년 (조건과 정확히 일치 - 포함되어야 함)
        RecruitmentResponseDTO exact = new RecruitmentResponseDTO(
            3L, "exp::3", "경력 3-5년 공고", "회사 3", "image_3.jpg",
            LocalDateTime.now().plusDays(30),
            3, 5, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
            LocalDateTime.now().minusDays(3)
        );
        allItems.add(exact);

        // 4. 경력 범위가 4-6년 (조건과 겹침 - 포함되어야 함)
        RecruitmentResponseDTO overlapping2 = new RecruitmentResponseDTO(
            4L, "exp::4", "경력 4-6년 공고", "회사 4", "image_4.jpg",
            LocalDateTime.now().plusDays(30),
            4, 6, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
            LocalDateTime.now().minusDays(4)
        );
        allItems.add(overlapping2);

        // 5. 경력 범위가 6-8년 (조건에 맞지 않음)
        allItems.add(new RecruitmentResponseDTO(
            5L, "exp::5", "경력 6-8년 공고", "회사 5", "image_5.jpg",
            LocalDateTime.now().plusDays(30),
            6, 8, "서울시", new BigDecimal("37.5"), new BigDecimal("127.0"),
            LocalDateTime.now().minusDays(5)
        ));

        // 조건에 맞는 공고만 포함하는 리스트
        List<RecruitmentResponseDTO> matchingItems = List.of(overlapping1, exact, overlapping2);

        // 레포지토리는 이미 경력 범위로 필터링한 결과를 반환해야 함
        Page<RecruitmentResponseDTO> mockPage = createMockResponsePage(matchingItems, page, 40);
        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(mockPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(3, result.getContent().size()); // 조건에 맞는 공고 3개

        // 모든 공고가 경력 범위 조건과 겹치는지 확인
        for (RecruitmentResponseDTO item : result.getContent()) {
            boolean rangeOverlaps = 
                (item.getExperienceMax() >= condition.getExperienceMin() && 
                 item.getExperienceMin() <= condition.getExperienceMax());

            assertTrue(rangeOverlaps, 
                    "모든 공고는 경력 범위 조건과 겹쳐야 합니다");
        }
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
                    "결과는 수정일 기준 내림차순으로 정렬되어야 합니다");
        }
    }

    // 테스트용 응답 DTO 리스트 생성 헬퍼 메소드
    private List<RecruitmentResponseDTO> createMockResponseList() {
        List<RecruitmentResponseDTO> responses = new ArrayList<>();

        // 첫 번째 응답 DTO
        responses.add(new RecruitmentResponseDTO(
                1L, "test::1001", "백엔드 개발자 모집", "ABC 회사", "company_image_url_1.jpg",
                LocalDateTime.of(2023, 12, 31, 23, 59),
                3, 5, "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now()
        ));

        // 두 번째 응답 DTO
        responses.add(new RecruitmentResponseDTO(
                2L, "test::1002", "프론트엔드 개발자 모집", "XYZ 회사", "company_image_url_2.jpg",
                LocalDateTime.of(2023, 11, 30, 23, 59),
                2, 4, "서울시 서초구", new BigDecimal("37.4"), new BigDecimal("127.1"),
                LocalDateTime.now()
        ));

        return responses;
    }

    // 테스트용 Page 객체 생성 헬퍼 메소드
    private Page<RecruitmentResponseDTO> createMockResponsePage(List<RecruitmentResponseDTO> content, int page, int size) {
        return new PageImpl<>(content, PageRequest.of(page, size), content.size());
    }

    // 수정일 기준 내림차순으로 정렬된 테스트용 응답 DTO 리스트 생성 헬퍼 메소드
    // 페이지네이션 테스트를 위해 8개의 아이템을 생성 (5개만 반환되어야 함)
    private List<RecruitmentResponseDTO> createSortedByModifiedAtMockResponseList() {
        List<RecruitmentResponseDTO> responses = new ArrayList<>();

        // 1. 가장 최근에 수정된 공고 (1일 전)
        responses.add(new RecruitmentResponseDTO(
                1L, "test::1001", "백엔드 개발자 모집", "ABC 회사", "company_image_url_1.jpg",
                LocalDateTime.of(2023, 12, 31, 23, 59),
                3, 5, "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0"),
                LocalDateTime.now().minusDays(1)
        ));

        // 2. 두 번째로 최근에 수정된 공고 (3일 전)
        responses.add(new RecruitmentResponseDTO(
                2L, "test::1002", "프론트엔드 개발자 모집", "XYZ 회사", "company_image_url_2.jpg",
                LocalDateTime.of(2023, 11, 30, 23, 59),
                2, 4, "서울시 서초구", new BigDecimal("37.4"), new BigDecimal("127.1"),
                LocalDateTime.now().minusDays(3)
        ));

        // 3. 세 번째로 최근에 수정된 공고 (5일 전)
        responses.add(new RecruitmentResponseDTO(
                3L, "test::1003", "데이터 엔지니어 모집", "DEF 회사", "company_image_url_3.jpg",
                LocalDateTime.of(2023, 12, 15, 23, 59),
                5, 7, "서울시 송파구", new BigDecimal("37.6"), new BigDecimal("127.2"),
                LocalDateTime.now().minusDays(5)
        ));

        // 4. 네 번째로 최근에 수정된 공고 (7일 전)
        responses.add(new RecruitmentResponseDTO(
                4L, "test::1004", "모바일 개발자 모집", "GHI 회사", "company_image_url_4.jpg",
                LocalDateTime.of(2023, 12, 20, 23, 59),
                4, 6, "서울시 마포구", new BigDecimal("37.55"), new BigDecimal("126.9"),
                LocalDateTime.now().minusDays(7)
        ));

        // 5. 다섯 번째로 최근에 수정된 공고 (10일 전)
        responses.add(new RecruitmentResponseDTO(
                5L, "test::1005", "DevOps 엔지니어 모집", "JKL 회사", "company_image_url_5.jpg",
                LocalDateTime.of(2023, 12, 25, 23, 59),
                2, 4, "서울시 영등포구", new BigDecimal("37.52"), new BigDecimal("126.93"),
                LocalDateTime.now().minusDays(10)
        ));

        // 6. 여섯 번째로 최근에 수정된 공고 (14일 전)
        responses.add(new RecruitmentResponseDTO(
                6L, "test::1006", "QA 엔지니어 모집", "MNO 회사", "company_image_url_6.jpg",
                LocalDateTime.of(2023, 12, 10, 23, 59),
                3, 5, "서울시 강동구", new BigDecimal("37.53"), new BigDecimal("127.12"),
                LocalDateTime.now().minusDays(14)
        ));

        // 7. 일곱 번째로 최근에 수정된 공고 (20일 전)
        responses.add(new RecruitmentResponseDTO(
                7L, "test::1007", "보안 엔지니어 모집", "PQR 회사", "company_image_url_7.jpg",
                LocalDateTime.of(2023, 12, 5, 23, 59),
                6, 8, "서울시 성동구", new BigDecimal("37.54"), new BigDecimal("127.05"),
                LocalDateTime.now().minusDays(20)
        ));

        // 8. 여덟 번째로 최근에 수정된 공고 (30일 전)
        responses.add(new RecruitmentResponseDTO(
                8L, "test::1008", "시스템 엔지니어 모집", "STU 회사", "company_image_url_8.jpg",
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
        Set<Tag> tags = new HashSet<>();
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
    @DisplayName("키워드로 채용 공고 필터링 테스트")
    void listRecruitments_WithKeyword_ShouldFilterByKeyword() {
        // Given
        String keyword = "백엔드";
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .keyword(keyword)
                .build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> keyword.equals(cond.getKeyword())),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("빈 키워드로 채용 공고 필터링 테스트")
    void listRecruitments_WithEmptyKeyword_ShouldPassNullKeyword() {
        // Given
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .keyword("")
                .build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> cond.getKeyword() == null),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("키워드와 다른 조건으로 채용 공고 필터링 테스트")
    void listRecruitments_WithKeywordAndOtherConditions_ShouldFilterCorrectly() {
        // Given
        String keyword = "개발자";
        RecruitmentSearchConditionDTO condition = RecruitmentSearchConditionDTO.builder()
                .keyword(keyword)
                .salaryMin(3000)
                .salaryMax(5000)
                .experienceMin(3)
                .experienceMax(7)
                .education(4)
                .techStacks(List.of(1L, 2L))
                .build();

        int page = 0;
        List<RecruitmentResponseDTO> expectedContent = createMockResponseList();
        Page<RecruitmentResponseDTO> expectedPage = createMockResponsePage(expectedContent, page, 40);

        when(recruitmentRepository.findBySearchConditions(any(RecruitmentSearchConditionDTO.class), any(Pageable.class)))
            .thenReturn(expectedPage);

        // When
        Page<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(condition, page);

        // Then
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(recruitmentRepository).findBySearchConditions(
            argThat(cond -> 
                keyword.equals(cond.getKeyword()) && 
                cond.getSalaryMin() == 3000 &&
                cond.getSalaryMax() == 5000 &&
                cond.getExperienceMin() == 3 &&
                cond.getExperienceMax() == 7 &&
                cond.getEducation() == 4 &&
                cond.getTechStackCount() == 2L
            ),
            argThat(pageable -> pageable.getPageNumber() == page && pageable.getPageSize() == 40)
        );
    }

    @Test
    @DisplayName("채용 공고 필터 옵션 조회 테스트")
    void getFilterOptions_ShouldReturnAllFilterOptions() throws Exception {
        // Given
        List<EducationOptionDTO> educationOptions = RecruitmentService.EDUCATION_OPTIONS;
        List<Integer> experienceOptions = RecruitmentService.EXPERIENCE_OPTIONS;
        List<String> workLocations = RecruitmentService.WORK_LOCATIONS;
        int defaultMinSalary = RecruitmentService.DEFAULT_MIN_SALARY;
        int defaultMaxSalary = RecruitmentService.DEFAULT_MAX_SALARY;

        List<TechItem> techItems = List.of(
                new TechItem(1L, "Java", "자바", "java_icon.png"),
                new TechItem(2L, "Spring", "스프링", "spring_icon.png"),
                new TechItem(3L, "Python", "파이썬", "python_icon.png")
        );

        List<TagItem> tagItems = List.of(
                new TagItem(1L, "신입"),
                new TagItem(2L, "경력"),
                new TagItem(3L, "재택근무")
        );

        when(techItemRepository.findAll()).thenReturn(techItems);
        when(tagItemRepository.findAll()).thenReturn(tagItems);

        // When
        RecruitmentFilterResponseDTO result = recruitmentService.getFilterOptions();

        // Then
        assertEquals(educationOptions.size(), result.getEducationOptions().size());
        for (int i = 0; i < educationOptions.size(); i++) {
            assertEquals(educationOptions.get(i).getLabel(), result.getEducationOptions().get(i).getLabel());
            assertEquals(educationOptions.get(i).getValue(), result.getEducationOptions().get(i).getValue());
        }

        assertEquals(experienceOptions.size(), result.getExperienceOptions().size());
        for (int i = 0; i < experienceOptions.size(); i++) {
            assertEquals(experienceOptions.get(i), result.getExperienceOptions().get(i));
        }

        assertEquals(3, result.getTechStacks().size());
        assertEquals("자바", result.getTechStacks().get(0).getLabelKo());
        assertEquals("Java", result.getTechStacks().get(0).getLabelEn());
        assertEquals("java_icon.png", result.getTechStacks().get(0).getIconUrl());

        assertEquals("스프링", result.getTechStacks().get(1).getLabelKo());
        assertEquals("Spring", result.getTechStacks().get(1).getLabelEn());
        assertEquals("spring_icon.png", result.getTechStacks().get(1).getIconUrl());

        assertEquals("파이썬", result.getTechStacks().get(2).getLabelKo());
        assertEquals("Python", result.getTechStacks().get(2).getLabelEn());
        assertEquals("python_icon.png", result.getTechStacks().get(2).getIconUrl());

        assertEquals(workLocations.size(), result.getWorkLocations().size());
        for (int i = 0; i < workLocations.size(); i++) {
            assertEquals(workLocations.get(i), result.getWorkLocations().get(i));
        }

        assertEquals(3, result.getTags().size());
        assertEquals("신입", result.getTags().get(0).getTagName());
        assertEquals("경력", result.getTags().get(1).getTagName());
        assertEquals("재택근무", result.getTags().get(2).getTagName());

        assertEquals(defaultMinSalary, result.getSalaryRange().getMin());
        assertEquals(defaultMaxSalary, result.getSalaryRange().getMax());

        verify(techItemRepository).findAll();
        verify(tagItemRepository).findAll();
    }

    private void setupCreateRecruitmentMocks() {

    }

    @Test
    @DisplayName("기술 스택이 없는 채용 공고 생성 - 성공")
    void createOrUpdateRecruitment_NoTechStacks_Success() {
        // given
        recruitmentRequestDTO.setRequiredSkills(null);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(recruitmentRepository.save(any(Recruitment.class))).thenReturn(recruitment);
        when(tagItemRepository.findAllByTagNameIn(anySet())).thenReturn(new ArrayList<>());
        when(tagItemRepository.saveAll(anyList())).thenReturn(List.of(tagItem1, tagItem2));
        when(tagRepository.saveAll(anySet())).thenReturn(List.of());

        // when
        RecruitmentResponseDTO result = recruitmentService.createOrUpdateRecruitment(recruitmentRequestDTO);

        // then
        assertThat(result).isNotNull();
        verify(techItemRepository, never()).findAllByEnglishNameIn(anySet());
        verify(techStackRepository, never()).saveAll(anySet());
    }

    @Test
    @DisplayName("태그가 없는 채용 공고 생성 - 성공")
    void createOrUpdateRecruitment_NoTags_Success() {
        // given
        recruitmentRequestDTO.setTags(null);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(recruitmentRepository.save(any(Recruitment.class))).thenReturn(recruitment);
        when(techItemRepository.findAllByEnglishNameIn(anySet())).thenReturn(new ArrayList<>());
        when(techItemRepository.saveAll(anyList())).thenReturn(List.of(techItem1, techItem2));
        when(techStackRepository.saveAll(anySet())).thenReturn(List.of());

        // when
        RecruitmentResponseDTO result = recruitmentService.createOrUpdateRecruitment(recruitmentRequestDTO);

        // then
        assertThat(result).isNotNull();
        verify(tagItemRepository, never()).findAllByTagNameIn(anySet());
        verify(tagRepository, never()).saveAll(anySet());
    }

    @Test
    @DisplayName("기존 기술 스택과 동일한 업데이트 - 변경사항 없음")
    void createOrUpdateRecruitment_SameTechStacks_NoChanges() {
        // given
        TechStack existingTechStack1 = TechStack.builder()
                .id(1L)
                .techItem(techItem1)
                .recruitment(recruitment)
                .build();
        TechStack existingTechStack2 = TechStack.builder()
                .id(2L)
                .techItem(techItem2)
                .recruitment(recruitment)
                .build();

        recruitment.getTechStacks().add(existingTechStack1);
        recruitment.getTechStacks().add(existingTechStack2);

        when(recruitmentRepository.findByRecruitmentSourceId(anyString())).thenReturn(Optional.of(recruitment));
        when(tagItemRepository.findAllByTagNameIn(anySet())).thenReturn(List.of(tagItem1, tagItem2));

        // when
        RecruitmentResponseDTO result = recruitmentService.createOrUpdateRecruitment(recruitmentRequestDTO);

        // then
        assertThat(result).isNotNull();
        verify(techStackRepository, never()).deleteAllByIdInBatch(anyList());
        verify(techItemRepository, never()).findAllByEnglishNameIn(anySet());
        verify(techStackRepository, never()).saveAll(anySet());
    }

    @Test
    @DisplayName("기존 태그와 동일한 업데이트 - 변경사항 없음")
    void createOrUpdateRecruitment_SameTags_NoChanges() {
        // given
        Tag existingTag1 = Tag.builder()
                .id(1L)
                .tagItem(tagItem1)
                .recruitment(recruitment)
                .build();
        Tag existingTag2 = Tag.builder()
                .id(2L)
                .tagItem(tagItem2)
                .recruitment(recruitment)
                .build();

        recruitment.getTags().add(existingTag1);
        recruitment.getTags().add(existingTag2);

        when(recruitmentRepository.findByRecruitmentSourceId(anyString())).thenReturn(Optional.of(recruitment));
        when(techItemRepository.findAllByEnglishNameIn(anySet())).thenReturn(List.of(techItem1, techItem2));

        // when
        RecruitmentResponseDTO result = recruitmentService.createOrUpdateRecruitment(recruitmentRequestDTO);

        // then
        assertThat(result).isNotNull();
        verify(tagRepository, never()).deleteAllByIdInBatch(anyList());
        verify(tagItemRepository, never()).findAllByTagNameIn(anySet());
        verify(tagRepository, never()).saveAll(anySet());
    }

    @Test
    @DisplayName("기술 스택 부분 업데이트 - 일부 삭제, 일부 추가")
    void createOrUpdateRecruitment_PartialTechStackUpdate_Success() {
        // given
        TechItem existingTechItem = TechItem.builder()
                .id(3L)
                .englishName("Python")
                .koreanName("파이썬")
                .build();

        TechStack existingTechStack = TechStack.builder()
                .id(1L)
                .techItem(existingTechItem)
                .recruitment(recruitment)
                .build();

        recruitment.getTechStacks().add(existingTechStack);

        when(recruitmentRepository.findByRecruitmentSourceId(anyString())).thenReturn(Optional.of(recruitment));
        when(techItemRepository.findAllByEnglishNameIn(anySet())).thenReturn(List.of(techItem1, techItem2));
        when(techStackRepository.saveAll(anySet())).thenReturn(List.of());
        when(tagItemRepository.findAllByTagNameIn(anySet())).thenReturn(List.of(tagItem1, tagItem2));

        // when
        RecruitmentResponseDTO result = recruitmentService.createOrUpdateRecruitment(recruitmentRequestDTO);

        // then
        assertThat(result).isNotNull();
        verify(techStackRepository).deleteAllByIdInBatch(anyList());
        verify(techStackRepository).saveAll(anySet());
    }
}
