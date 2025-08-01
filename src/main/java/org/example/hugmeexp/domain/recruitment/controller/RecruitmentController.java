package org.example.hugmeexp.domain.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentCompanySearchResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentRequestDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.service.CompanyService;
import org.example.hugmeexp.domain.recruitment.service.RecruitmentService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j    // 로깅 어노테이션
@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitments" , description = "채용 공고 관련 API")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final CompanyService companyService;

    @Operation(summary = "채용 공고 목록 조회", description = "채용 공고 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<Response<List<RecruitmentResponseDTO>>> listRecruitments(
            @Valid @ModelAttribute RecruitmentSearchConditionDTO conditionDTO) {

        List<RecruitmentResponseDTO> result = recruitmentService.listRecruitments(conditionDTO);

        Response<List<RecruitmentResponseDTO>> response = Response.<List<RecruitmentResponseDTO>>builder()
                .message("채용 공고 목록 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "채용 기업 목록 조회", description = "채용 기업 목록을 조회합니다")
    @GetMapping("/companies")
    public ResponseEntity<Response<List<RecruitmentCompanySearchResponseDTO>>> searchCompanies(
            @RequestParam(required = false) String keyword
    ){

        List<RecruitmentCompanySearchResponseDTO> result = companyService.searchCompaniesByKeyword(keyword);

        if(result.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        Response<List<RecruitmentCompanySearchResponseDTO>> response = Response.<List<RecruitmentCompanySearchResponseDTO>>builder()
                .message("기업 목록 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "최신 채용 공고 조회", description = "홈 화면에 보여질 최신 채용 공고 목록을 조회합니다 (최신 수정일 기준)")
    @GetMapping("/home")
    public ResponseEntity<Response<List<RecruitmentResponseDTO>>> findLatestRecruitments() {
        int limit = 5;
        List<RecruitmentResponseDTO> result = recruitmentService.findLatestRecruitments(limit);

        Response<List<RecruitmentResponseDTO>> response = Response.<List<RecruitmentResponseDTO>>builder()
                .message("최신 채용 공고 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "채용 공고 스크래핑",
            description = "외부 사이트에서 스크래핑한 채용 공고 데이터를 생성하거나 업데이트합니다. " +
                    "동일한 sourceId가 존재하면 업데이트하고, 없으면 새로 생성합니다."
    )
    @PostMapping("/scrape")
    public ResponseEntity<Response<RecruitmentResponseDTO>> createRecruitment(
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody RecruitmentRequestDTO requestDTO) {

        // API 키 인증
        if (!"1234567890".equals(apiKey)) {
            Response<RecruitmentResponseDTO> errorResponse = Response.<RecruitmentResponseDTO>builder()
                    .message("유효하지 않은 API 키입니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        RecruitmentResponseDTO createdRecruitment = recruitmentService.createOrUpdateRecruitment(requestDTO);

        Response<RecruitmentResponseDTO> response = Response.<RecruitmentResponseDTO>builder()
                .message("채용 공고 생성 성공")
                .data(createdRecruitment)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
