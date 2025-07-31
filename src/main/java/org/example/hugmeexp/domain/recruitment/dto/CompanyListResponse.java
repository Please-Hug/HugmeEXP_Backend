package org.example.hugmeexp.domain.recruitment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "채용 기업 목록 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CompanyListResponse {

    @Schema(description = "응답 메시지", example = "채용 기업 목록 조회 성공")
    private String message;

    @Schema(description = "채용 기업 목록 데이터")
    private List<RecruitmentCompanySearchResponseDTO> data;

}
