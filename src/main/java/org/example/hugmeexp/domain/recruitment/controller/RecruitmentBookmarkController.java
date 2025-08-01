package org.example.hugmeexp.domain.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.exception.DuplicateRecruitmentBookmarkException;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentNotFoundException;
import org.example.hugmeexp.domain.recruitment.service.RecruitmentBookmarkService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j    // 로깅 어노테이션
@RestController
@RequestMapping("/api/recruitments/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmarks" , description = "즐겨 찾기 관련 API")
public class RecruitmentBookmarkController {

    private final RecruitmentBookmarkService recruitmentBookmarkService;

    @Operation(
        summary = "즐겨찾기 등록",
        description = "사용자가 특정 채용 공고를 즐겨찾기에 등록합니다.",
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    description = "즐겨찾기 등록 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                    responseCode = "400",
                    description = "이미 등록된 즐겨찾기입니다",
                    content = @Content(schema = @Schema(implementation = DuplicateRecruitmentBookmarkException.class))
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자 또는 채용 공고 ID",
                    content = @Content(schema = @Schema(implementation = RecruitmentNotFoundException.class))
                )
            }
    )
    @PostMapping("/{recruitmentId}")
    public ResponseEntity<Response<Void>> addBookmark(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        recruitmentBookmarkService.addBookmark(userDetails.getUser().getId(), recruitmentId);

        Response<Void> response = Response.<Void>builder()
                .message("즐겨 찾기 등록 완료")
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "즐겨찾기 취소",
            description = "사용자가 특정 채용 공고의 즐겨찾기를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "즐겨찾기 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "즐겨찾기 또는 채용 공고가 존재하지 않음")
            }
    )
    @DeleteMapping("/{recruitmentId}")
    public ResponseEntity<Response<Void>> deleteBookmark(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        recruitmentBookmarkService.removeBookmark(userDetails.getUser().getId(), recruitmentId);

        Response<Void> response = Response.<Void>builder()
                .message("즐겨찾기 취소 완료")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "즐겨찾기 목록 조회",
        description = "로그인한 사용자의 즐겨찾기 채용 공고 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "즐겨찾기 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = RecruitmentResponseDTO.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "서버 오류"
            )
        }
    )
    @GetMapping
    public ResponseEntity<Response<List<RecruitmentResponseDTO>>> getRecruitmentBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<RecruitmentResponseDTO> recruitmentBookmarks = recruitmentBookmarkService.getRecruitmentBookmarks(userDetails.getUser().getId());

        Response<List<RecruitmentResponseDTO>> response = Response.<List<RecruitmentResponseDTO>>builder()
                .message("즐겨찾기 목록 조회 성공")
                .data(recruitmentBookmarks)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
