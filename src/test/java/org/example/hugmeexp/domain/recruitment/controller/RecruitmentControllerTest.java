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
        mockMvc = MockMvcBuilders.standaloneSetup(recruitmentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // LocalDateTime 직렬화를 위해

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

        // 테스트용 RecruitmentResponseDTO 생성
        recruitmentResponseDTO = RecruitmentResponseDTO.builder()
                .id(1L)
                .title("백엔드 개발자 모집")
                .experience(2)
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
        mockMvc.perform(post("/api/recruitments/scrape")
                        .header("X-API-Key", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("채용 공고 생성 성공"))
                .andExpect(jsonPath("$.data.title").value("백엔드 개발자 모집"))
                .andExpect(jsonPath("$.data.experience").value(2))
                .andExpect(jsonPath("$.data.workLocation").value("서울시 강남구"));
    }

    @Test
    @DisplayName("잘못된 API 키로 채용 공고 스크래핑 - 실패")
    void scrapeRecruitment_InvalidApiKey_Unauthorized() throws Exception {
        // when & then
        mockMvc.perform(post("/api/recruitments/scrape")
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
        mockMvc.perform(post("/api/recruitments/scrape")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 요청 데이터로 채용 공고 스크래핑 - 실패")
    void scrapeRecruitment_InvalidRequestData_BadRequest() throws Exception {
        // given - 필수 필드가 누락된 요청 데이터
        RecruitmentRequestDTO invalidRequest = new RecruitmentRequestDTO();
        invalidRequest.setSourceId(""); // 빈 값
        // 다른 필수 필드들 누락

        // when & then
        mockMvc.perform(post("/api/recruitments/scrape")
                        .header("X-API-Key", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
