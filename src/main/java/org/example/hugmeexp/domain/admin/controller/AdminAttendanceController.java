package org.example.hugmeexp.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.admin.dto.response.ActiveUsersStatsResponse;
import org.example.hugmeexp.domain.admin.service.AdminAttendanceService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin Attendance", description = "관리자 전용 출석 관리 API")
@RestController
@RequestMapping("/api/v1/admin/attendance")
@RequiredArgsConstructor
public class AdminAttendanceController {

    private final AdminAttendanceService adminAttendanceService;

    @Operation(summary = "출석날짜 리스트 조회", description = "특정 사용자의 모든 출석 날짜를 조회")
    @GetMapping("/dates")
    public ResponseEntity<Response<List<String>>> getAttendanceDates(
            @RequestParam String username) {
        List<LocalDate> dates = adminAttendanceService.getAllAttendanceDatesByAdmin(username);
        List<String> dateStrings = dates.stream()
                .map(LocalDate::toString)
                .toList();
        return ResponseEntity.ok(Response.<List<String>>builder()
                .message("출석 날짜 리스트 조회 성공")
                .data(dateStrings)
                .build());
    }

    @Operation(
            summary = "활성 사용자 통계 조회",
            description = "최근 30일 내 출석한 사용자를 기준으로 활성 사용자 통계를 조회"
    )
    @GetMapping("/active-stats")
    public ResponseEntity<Response<ActiveUsersStatsResponse>> getActiveUsersStats() {
        ActiveUsersStatsResponse stats = adminAttendanceService.getActiveUsersStats();
        return ResponseEntity.ok(Response.<ActiveUsersStatsResponse>builder()
                .message("활성 사용자 통계 조회 성공")
                .data(stats)
                .build());
    }
}