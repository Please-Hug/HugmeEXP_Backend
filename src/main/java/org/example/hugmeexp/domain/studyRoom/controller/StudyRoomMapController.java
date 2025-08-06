package org.example.hugmeexp.domain.studyRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomEnums;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationTimeResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyRoomDetailResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.TimeSlotResponse;
import org.example.hugmeexp.domain.studyRoom.service.StudyHallSearchService;
import org.example.hugmeexp.domain.studyRoom.service.StudyHallService;
import org.example.hugmeexp.domain.studyRoom.service.StudyRoomService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Tag(name = "StudyRoom - Search & Map", description = "스터디룸 검색 및 지도 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studyroom")
public class StudyRoomMapController {

    private final StudyHallService studyHallService;
    private final StudyRoomService studyRoomService;
    private final StudyHallSearchService searchService;

    // === 지도 관련 API ===

    @Operation(summary = "모든 스터디홀 위치 조회", description = "지도에 표시할 모든 스터디홀의 위치 정보를 조회합니다.")
    @GetMapping("/map/halls")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> getAllStudyHallsForMap() {
        List<StudyHallLocationResponse> studyHalls = studyHallService.getAllStudyHallsForMap();

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message("모든 스터디홀 위치 정보를 조회했습니다.")
                .data(studyHalls)
                .build());
    }

    // === 검색 관련 API ===

    @Operation(summary = "통합 스터디홀 검색",
            description = "키워드, 위치, 필터 조건을 활용한 통합 검색을 제공합니다.")
    @PostMapping("/search")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHalls(
            @Valid @RequestBody StudyHallSearchRequest request) {

        try {
            // 요청 검증
            validateSearchRequest(request);

            List<StudyHallLocationResponse> results = searchService.searchStudyHalls(
                    request,
                    request.getSortType(),
                    request.getFilters()
            );

            String message = buildSearchResultMessage(request, results.size());

            return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                    .message(message)
                    .data(results)
                    .build());

        } catch (IllegalArgumentException e) {
            log.warn("잘못된 검색 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Response.<List<StudyHallLocationResponse>>builder()
                            .message("검색 요청이 올바르지 않습니다: " + e.getMessage())
                            .data(List.of())
                            .build());
        }
    }

    @Operation(summary = "스마트 키워드 검색",
            description = "유사도 기반 스마트 검색으로 더 정확한 결과를 제공합니다.")
    @GetMapping("/search/smart")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> smartSearch(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,
            @Parameter(description = "결과 개수", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {

        if (!StringUtils.hasText(keyword)) {
            return ResponseEntity.badRequest()
                    .body(Response.<List<StudyHallLocationResponse>>builder()
                            .message("검색 키워드는 필수입니다.")
                            .data(List.of())
                            .build());
        }

        List<StudyHallLocationResponse> results = searchService.smartKeywordSearch(keyword, limit);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("'%s' 검색 결과 %d개를 찾았습니다.", keyword, results.size()))
                .data(results)
                .build());
    }

    @Operation(summary = "검색 자동완성", description = "입력한 키워드에 대한 자동완성 제안을 제공합니다.")
    @GetMapping("/search/autocomplete")
    public ResponseEntity<Response<List<String>>> getAutocompleteSuggestions(
            @Parameter(description = "자동완성 검색어", required = true)
            @RequestParam String query,
            @Parameter(description = "제안 개수", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {

        List<String> suggestions = searchService.getAutocompleteSuggestions(query, limit);

        return ResponseEntity.ok(Response.<List<String>>builder()
                .message("자동완성 제안을 조회했습니다.")
                .data(suggestions)
                .build());
    }

    @Operation(summary = "현재 위치 기반 주변 스터디홀 검색",
            description = "현재 위치를 기준으로 지정된 반경 내의 스터디홀을 거리 순으로 조회합니다.")
    @PostMapping("/map/nearby")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchNearbyStudyHalls(
            @Valid @RequestBody StudyHallSearchRequest request) {

        if (!request.hasValidLocationInfo()) {
            return ResponseEntity.badRequest()
                    .body(Response.<List<StudyHallLocationResponse>>builder()
                            .message("유효한 위치 정보가 필요합니다.")
                            .data(List.of())
                            .build());
        }

        log.info("Nearby study halls search requested - lat: {}, lng: {}, radius: {}km",
                request.getLatitude(), request.getLongitude(), request.getRadius());

        List<StudyHallLocationResponse> nearbyHalls = studyHallService.searchNearbyStudyHalls(request);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("반경 %.1fkm 내 스터디홀 %d개를 찾았습니다.",
                        request.getRadius(), nearbyHalls.size()))
                .data(nearbyHalls)
                .build());
    }

    // === 스터디홀 상세 정보 API ===

    @Operation(summary = "특정 스터디홀 상세 정보 조회",
            description = "특정 스터디홀의 상세 정보를 조회합니다.")
    @GetMapping("/halls/{studyHallId}")
    public ResponseEntity<Response<StudyHallLocationResponse>> getStudyHallDetail(
            @Parameter(description = "스터디홀 ID", required = true) @PathVariable Long studyHallId) {

        try {
            StudyHallLocationResponse studyHall = studyHallService.getStudyHallDetail(studyHallId);

            return ResponseEntity.ok(Response.<StudyHallLocationResponse>builder()
                    .message("스터디홀 상세 정보를 조회했습니다.")
                    .data(studyHall)
                    .build());

        } catch (Exception e) {
            log.error("스터디홀 조회 중 오류 발생 - ID: {}", studyHallId, e);
            return ResponseEntity.badRequest()
                    .body(Response.<StudyHallLocationResponse>builder()
                            .message("존재하지 않는 스터디홀입니다.")
                            .build());
        }
    }

    @Operation(summary = "현재 위치로부터 특정 스터디홀까지의 거리 계산",
            description = "현재 위치를 기준으로 특정 스터디홀까지의 거리를 계산하여 반환합니다.")
    @GetMapping("/halls/{studyHallId}/distance")
    public ResponseEntity<Response<StudyHallLocationResponse>> getStudyHallWithDistance(
            @Parameter(description = "스터디홀 ID", required = true) @PathVariable Long studyHallId,
            @Parameter(description = "현재 위치 위도", required = true) @RequestParam Double latitude,
            @Parameter(description = "현재 위치 경도", required = true) @RequestParam Double longitude) {

        try {
            StudyHallLocationResponse studyHall = studyHallService.getStudyHallWithDistance(
                    studyHallId, latitude, longitude);

            return ResponseEntity.ok(Response.<StudyHallLocationResponse>builder()
                    .message("스터디홀 정보와 거리를 계산했습니다.")
                    .data(studyHall)
                    .build());

        } catch (Exception e) {
            log.error("거리 계산 중 오류 발생 - ID: {}, lat: {}, lng: {}", studyHallId, latitude, longitude, e);
            return ResponseEntity.badRequest()
                    .body(Response.<StudyHallLocationResponse>builder()
                            .message("거리 계산에 실패했습니다.")
                            .build());
        }
    }

    // === 스터디룸 관련 API ===

    @Operation(summary = "특정 스터디홀의 모든 스터디룸 조회",
            description = "특정 스터디홀에 속한 모든 스터디룸 목록을 조회합니다.")
    @GetMapping("/halls/{studyHallId}/rooms")
    public ResponseEntity<Response<List<StudyRoomDetailResponse>>> getStudyRoomsInHall(
            @Parameter(description = "스터디홀 ID", required = true) @PathVariable Long studyHallId) {

        try {
            List<StudyRoomDetailResponse> studyRooms = studyRoomService.getStudyRoomsInHall(studyHallId);

            return ResponseEntity.ok(Response.<List<StudyRoomDetailResponse>>builder()
                    .message("스터디룸 목록을 조회했습니다.")
                    .data(studyRooms)
                    .build());

        } catch (Exception e) {
            log.error("스터디룸 목록 조회 중 오류 발생 - Hall ID: {}", studyHallId, e);
            return ResponseEntity.badRequest()
                    .body(Response.<List<StudyRoomDetailResponse>>builder()
                            .message("스터디룸 목록 조회에 실패했습니다.")
                            .data(List.of())
                            .build());
        }
    }

    @Operation(summary = "특정 스터디룸 상세 정보 조회",
            description = "특정 스터디룸의 상세 정보를 조회합니다.")
    @GetMapping("/rooms/{studyRoomId}")
    public ResponseEntity<Response<StudyRoomDetailResponse>> getStudyRoomDetail(
            @Parameter(description = "스터디룸 ID", required = true) @PathVariable Long studyRoomId) {

        try {
            StudyRoomDetailResponse studyRoom = studyRoomService.getStudyRoomDetail(studyRoomId);

            return ResponseEntity.ok(Response.<StudyRoomDetailResponse>builder()
                    .message("스터디룸 상세 정보를 조회했습니다.")
                    .data(studyRoom)
                    .build());

        } catch (Exception e) {
            log.error("스터디룸 상세 조회 중 오류 발생 - Room ID: {}", studyRoomId, e);
            return ResponseEntity.badRequest()
                    .body(Response.<StudyRoomDetailResponse>builder()
                            .message("스터디룸 정보 조회에 실패했습니다.")
                            .build());
        }
    }

    // === 예약 시간 관련 API ===

    @Operation(summary = "특정 스터디룸의 예약 가능한 시간 조회",
            description = "특정 날짜의 스터디룸 예약 가능한 시간대를 조회합니다.")
    @GetMapping("/rooms/{studyRoomId}/available-times")
    public ResponseEntity<Response<List<TimeSlotResponse>>> getAvailableTimeSlots(
            @Parameter(description = "스터디룸 ID", required = true) @PathVariable Long studyRoomId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        try {
            List<TimeSlotResponse> availableSlots = studyRoomService.getAvailableTimeSlots(studyRoomId, date);

            return ResponseEntity.ok(Response.<List<TimeSlotResponse>>builder()
                    .message("예약 가능한 시간대를 조회했습니다.")
                    .data(availableSlots)
                    .build());

        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(Response.<List<TimeSlotResponse>>builder()
                            .message("잘못된 날짜 형식입니다.")
                            .data(List.of())
                            .build());
        } catch (Exception e) {
            log.error("예약 가능 시간 조회 중 오류 발생 - Room ID: {}, Date: {}", studyRoomId, date, e);
            return ResponseEntity.badRequest()
                    .body(Response.<List<TimeSlotResponse>>builder()
                            .message("예약 가능 시간 조회에 실패했습니다.")
                            .data(List.of())
                            .build());
        }
    }

    @Operation(summary = "특정 날짜의 스터디룸 예약 현황 조회",
            description = "특정 날짜의 스터디룸 예약 현황을 조회합니다.")
    @GetMapping("/rooms/{studyRoomId}/reservations")
    public ResponseEntity<Response<List<ReservationTimeResponse>>> getReservationsByDate(
            @Parameter(description = "스터디룸 ID", required = true) @PathVariable Long studyRoomId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", required = true) @RequestParam String date) {

        try {
            // 날짜 형식 검증
            LocalDate.parse(date);

            List<ReservationTimeResponse> reservations = studyRoomService.getReservationsByDate(studyRoomId, date);

            return ResponseEntity.ok(Response.<List<ReservationTimeResponse>>builder()
                    .message("예약 현황을 조회했습니다.")
                    .data(reservations)
                    .build());

        } catch (DateTimeException e) {
            return ResponseEntity.badRequest()
                    .body(Response.<List<ReservationTimeResponse>>builder()
                            .message("잘못된 날짜 형식입니다. yyyy-MM-dd 형식을 사용해주세요.")
                            .data(List.of())
                            .build());
        } catch (Exception e) {
            log.error("예약 현황 조회 중 오류 발생 - Room ID: {}, Date: {}", studyRoomId, date, e);
            return ResponseEntity.badRequest()
                    .body(Response.<List<ReservationTimeResponse>>builder()
                            .message("예약 현황 조회에 실패했습니다.")
                            .data(List.of())
                            .build());
        }
    }

    // === Legacy API (하위 호환성) ===

    @Operation(summary = "주소로 스터디홀 검색 (Legacy)", description = "주소를 기준으로 스터디홀을 검색합니다.")
    @GetMapping("/search/address")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHallsByAddress(
            @Parameter(description = "검색할 주소", required = true) @RequestParam String address) {

        List<StudyHallLocationResponse> studyHalls = studyHallService.searchStudyHallsByAddress(address);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("주소 '%s'로 %d개의 스터디홀을 찾았습니다.", address, studyHalls.size()))
                .data(studyHalls)
                .build());
    }

    @Operation(summary = "이름으로 스터디홀 검색 (Legacy)", description = "이름을 기준으로 스터디홀을 검색합니다.")
    @GetMapping("/search/name")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHallsByName(
            @Parameter(description = "검색할 스터디홀 이름", required = true) @RequestParam String name) {

        List<StudyHallLocationResponse> studyHalls = studyHallService.searchStudyHallsByName(name);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("이름 '%s'로 %d개의 스터디홀을 찾았습니다.", name, studyHalls.size()))
                .data(studyHalls)
                .build());
    }

    // === Private Helper Methods ===

    private void validateSearchRequest(StudyHallSearchRequest request) {
        // 위치 정보와 키워드가 모두 없는 경우
        if (!request.isLocationBasedSearch() && !request.isKeywordBasedSearch()) {
            throw new IllegalArgumentException("검색 키워드 또는 위치 정보 중 하나는 필수입니다.");
        }

        // 인원 범위 검증
        if (!request.hasValidCapacityRange()) {
            throw new IllegalArgumentException("최소 인원은 최대 인원보다 클 수 없습니다.");
        }

        // 시간대 검증
        try {
            request.getValidatedTimeZone();
        } catch (Exception e) {
            throw new IllegalArgumentException("지원하지 않는 타임존입니다: " + request.getTimeZoneId());
        }
    }

    private String buildSearchResultMessage(StudyHallSearchRequest request, int resultCount) {
        StringBuilder message = new StringBuilder();

        if (request.isKeywordBasedSearch()) {
            message.append(String.format("'%s' 키워드로 ", request.getKeyword()));
        }

        if (request.isLocationBasedSearch()) {
            message.append(String.format("반경 %.1fkm 내 ", request.getRadius()));
        }

        if (request.hasFilters()) {
            message.append("필터 조건으로 ");
        }

        message.append(String.format("%d개의 스터디홀을 찾았습니다.", resultCount));

        return message.toString();
    }
}