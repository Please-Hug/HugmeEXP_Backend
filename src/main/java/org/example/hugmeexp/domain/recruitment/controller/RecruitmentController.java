package org.example.hugmeexp.domain.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.*;
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

    @Operation(
        summary = "채용 공고 목록 조회",
        description = "조건에 따라 채용 공고 목록을 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "채용 공고 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecruitmentListResponse.class)
                )
            )
        }
    )
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

    @Operation(
        summary = "회사명 또는 공고 제목 키워드로 채용 공고 검색",
        description = "사용자가 입력한 키워드를 기준으로 회사명 또는 공고 제목에 포함되는 채용 공고 목록을 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "공고 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecruitmentCompanySearchResponseDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "204",
                description = "조건에 맞는 결과 없음"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 파라미터"
            )
        }
    )
    @GetMapping("/companies")
    public ResponseEntity<Response<List<RecruitmentCompanySearchResponseDTO>>> searchCompanies(
            @RequestParam(required = false) String keyword
    ){

        int limit = 5;
        List<RecruitmentCompanySearchResponseDTO> result = recruitmentService.findByKeyword(keyword,limit);

        if(result.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        Response<List<RecruitmentCompanySearchResponseDTO>> response = Response.<List<RecruitmentCompanySearchResponseDTO>>builder()
                .message("회사 이름 또는 제목 키워드로 공고 목록 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "최신 채용 공고 조회",
            description = "홈 화면에 보여질 최신 채용 공고 목록을 조회합니다 (최신 수정일 기준)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "최신 채용 공고 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecruitmentListResponse.class)
                            )
                    )
            }
    )
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
        summary = "채용 공고 상세 조회",
        description = "채용 공고의 ID를 기준으로 상세 정보를 조회합니다",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "채용 공고 상세 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RecruitmentDetailResponseDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "해당 ID의 채용 공고가 존재하지 않음"
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Response<RecruitmentDetailResponseDTO>> findRecruitmentDetail(@PathVariable Long id) {

        RecruitmentDetailResponseDTO result = recruitmentService.getRecruitmentDetail(id);

        Response<RecruitmentDetailResponseDTO> response = Response.<RecruitmentDetailResponseDTO>builder()
                .message("채용 공고 상세 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
        summary = "채용 공고 필터 옵션 조회",
        description = "채용 목록에서 사용할 수 있는 필터링 조건들을 조회합니다.\n\n" +
            "- 학력, 경력, 기술스택, 근무지, 태그, 연봉 범위 포함\n" +
            "- 프론트 필터 렌더링용 메타 데이터로 활용",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "필터 옵션 정상 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = RecruitmentFilterResponseDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류"
            )
        }
    )
    @GetMapping("/filters")
    public ResponseEntity<Response<RecruitmentFilterResponseDTO>> getFilters(){

        RecruitmentFilterResponseDTO result = recruitmentService.getFilterOptions();

        Response<RecruitmentFilterResponseDTO> response = Response.<RecruitmentFilterResponseDTO>builder()
                .message("채용 공고 필터 옵션 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
