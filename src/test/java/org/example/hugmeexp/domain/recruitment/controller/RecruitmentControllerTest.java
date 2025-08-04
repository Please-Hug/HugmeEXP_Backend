package org.example.hugmeexp.domain.recruitment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.recruitment.dto.CompanyRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.TechItemRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.TagRequestDTO;
import org.example.hugmeexp.domain.recruitment.enums.SourceType;
import org.example.hugmeexp.domain.recruitment.service.RecruitmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecruitmentController recruitmentController;

    @Mock
    private RecruitmentService recruitmentService;

    private RecruitmentRequestDTO recruitmentRequestDTO;
    private RecruitmentResponseDTO recruitmentResponseDTO;

    @BeforeEach
    void setUp() {
        // API 키를 ReflectionTestUtils로 직접 주입
        ReflectionTestUtils.setField(recruitmentController, "validApiKey", "1234567890");

        mockMvc = MockMvcBuilders.standaloneSetup(recruitmentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해

        // 테스트용 CompanyRequestDTO 생성 (검증 규칙에 맞게 수정)
        CompanyRequestDTO companyRequestDTO = CompanyRequestDTO.builder()
                .companyName("테스트 회사") // @NotBlank, @Size(max = 200)
                .companyAddress("서울시 강남구 테헤란로 123") // @Size(max = 500)
                .establishmentDate(LocalDateTime.now().toLocalDate()) // @PastOrPresent
                .companyDescription("테스트 회사 설명")
                .companyImageUrl("https://test.com/image.jpg") // @Size(max = 1000)
                .latitude(new BigDecimal("37.56650000")) // @Digits(integer = 2, fraction = 8)
                .longitude(new BigDecimal("126.97800000")) // @Digits(integer = 3, fraction = 8)
                .build();

        // 테스트용 TechItemRequestDTO 생성 (검증 규칙에 맞게 수정)
        List<TechItemRequestDTO> techItems = List.of(
                new TechItemRequestDTO("Java", "자바", "https://icon.com/java.png"),
                new TechItemRequestDTO("Spring", "스프링", "https://icon.com/spring.png")
        );

        // 테스트용 TagRequestDTO 생성 (검증 규칙에 맞게 수정)
        List<TagRequestDTO> tags = List.of(
                new TagRequestDTO("신입"),
                new TagRequestDTO("정규직")
        );

        // 테스트용 RecruitmentRequestDTO 생성 (모든 필수 필드와 검증 규칙에 맞게 수정)
        recruitmentRequestDTO = new RecruitmentRequestDTO();
        recruitmentRequestDTO.setRecruitmentSourceId("TEST_001"); // @NotBlank
        recruitmentRequestDTO.setTitle("백엔드 개발자 모집"); // @NotBlank, @Size(max = 500)
        recruitmentRequestDTO.setEducation(4); // @Min(0), @Max(10)
        recruitmentRequestDTO.setExperienceMin(2); // @Min(0)
        recruitmentRequestDTO.setExperienceMax(5); // @Min(0)
        recruitmentRequestDTO.setQualification("컴퓨터 관련 전공"); // @Size(max = 10000)
        recruitmentRequestDTO.setAdvantage("Spring 프레임워크 경험"); // @Size(max = 10000)
        recruitmentRequestDTO.setWelfare("4대 보험, 점심 제공"); // @Size(max = 10000)
        recruitmentRequestDTO.setWorkLocation("서울시 강남구"); // @NotBlank, @Size(max = 500)
        recruitmentRequestDTO.setLatitude(new BigDecimal("37.56650000")); // @Digits(integer = 2, fraction = 8)
        recruitmentRequestDTO.setLongitude(new BigDecimal("126.97800000")); // @Digits(integer = 3, fraction = 8)
        recruitmentRequestDTO.setSalaryMin(3000); // @NotNull, @Min(0)
        recruitmentRequestDTO.setSalaryMax(5000); // @NotNull, @Min(0)
        recruitmentRequestDTO.setLink("https://test-company.com/jobs/1");
        recruitmentRequestDTO.setSource(SourceType.WANTED); // @NotNull
        recruitmentRequestDTO.setDueDate(LocalDateTime.now().plusDays(30));
        recruitmentRequestDTO.setCompany(companyRequestDTO); // @NotNull, @Valid
        recruitmentRequestDTO.setRequiredSkills(techItems); // @Valid
        recruitmentRequestDTO.setTags(tags); // @Valid

        // 테스트용 RecruitmentResponseDTO 생성
        recruitmentResponseDTO = RecruitmentResponseDTO.builder()
                .id(1L)
                .title("백엔드 개발자 모집")
                .experienceMin(2)
                .experienceMax(2)
                .workLocation("서울시 강남구")
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .dueDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    @DisplayName("올바른 API 키로 채용 공고 스크래핑 - 성공")
    void scrapeRecruitment_ValidApiKey_Success() throws Exception {
        // given
        when(recruitmentService.createOrUpdateRecruitment(any(RecruitmentRequestDTO.class)))
                .thenReturn(recruitmentResponseDTO);

        // when & then
        mockMvc.perform(post("/api/v1/recruitments/scrape")
                        .header("X-API-Key", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("채용 공고 생성 성공"))
                .andExpect(jsonPath("$.data.title").value("백엔드 개발자 모집"))
                .andExpect(jsonPath("$.data.experienceMin").value(2))
                .andExpect(jsonPath("$.data.experienceMax").value(2))
                .andExpect(jsonPath("$.data.workLocation").value("서울시 강남구"));
    }

    @Test
    @DisplayName("잘못된 API 키로 채용 공고 스크래핑 - 실패")
    void scrapeRecruitment_InvalidApiKey_Unauthorized() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/recruitments/scrape")
                        .header("X-API-Key", "invalid-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("유효하지 않은 API 키입니다."));
    }

    @Test
    @DisplayName("API 키 없이 채용 공고 스크래핑 - 실패")
    void scrapeRecruitment_NoApiKey_BadRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/recruitments/scrape")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 요청 데이터로 채용 공고 스크래핑 - 실패")
    void scrapeRecruitment_InvalidRequestData_BadRequest() throws Exception {
        // given - 필수 필드가 누락된 요청 데이터
        RecruitmentRequestDTO invalidRequest = new RecruitmentRequestDTO();
        invalidRequest.setRecruitmentSourceId(""); // 빈 값
        // 다른 필수 필드들 누락

        // when & then
        mockMvc.perform(post("/api/v1/recruitments/scrape")
                        .header("X-API-Key", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
