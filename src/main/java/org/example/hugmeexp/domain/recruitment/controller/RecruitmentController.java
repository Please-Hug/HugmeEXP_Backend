package org.example.hugmeexp.domain.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.service.RecruitmentService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j    // 로깅 어노테이션
@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitments" , description = "채용 공고 관련 API")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

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
}
