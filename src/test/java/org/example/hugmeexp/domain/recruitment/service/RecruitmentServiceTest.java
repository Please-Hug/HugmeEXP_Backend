package org.example.hugmeexp.domain.recruitment.service;

import org.example.hugmeexp.domain.recruitment.dto.CompanyRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.dto.TechItemRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.TagRequestDTO;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.entity.Tag;
import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.example.hugmeexp.domain.recruitment.entity.TechStack;
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
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        recruitmentRequestDTO.setSourceId("TEST_001");
        recruitmentRequestDTO.setTitle("백엔드 개발자 모집");
        recruitmentRequestDTO.setEducation(4);
        recruitmentRequestDTO.setExperience(2);
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
                .sourceId("TEST_001")
                .title("백엔드 개발자 모집")
                .education(4)
                .experience(2)
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
                .tags(new ArrayList<>())
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

    @Test
    @DisplayName("기술 스택이 없는 채용 공고 생성 - 성공")
    void createOrUpdateRecruitment_NoTechStacks_Success() {
        // given
        recruitmentRequestDTO.setRequiredSkills(null);
        when(recruitmentRepository.existsRecruitmentBySourceId(anyString())).thenReturn(false);
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
        when(recruitmentRepository.existsRecruitmentBySourceId(anyString())).thenReturn(false);
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

        when(recruitmentRepository.existsRecruitmentBySourceId(anyString())).thenReturn(true);
        when(recruitmentRepository.findBySourceId(anyString())).thenReturn(Optional.of(recruitment));
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

        when(recruitmentRepository.existsRecruitmentBySourceId(anyString())).thenReturn(true);
        when(recruitmentRepository.findBySourceId(anyString())).thenReturn(Optional.of(recruitment));
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

        when(recruitmentRepository.existsRecruitmentBySourceId(anyString())).thenReturn(true);
        when(recruitmentRepository.findBySourceId(anyString())).thenReturn(Optional.of(recruitment));
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
