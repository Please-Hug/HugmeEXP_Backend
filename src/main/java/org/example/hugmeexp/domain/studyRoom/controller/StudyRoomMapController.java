package org.example.hugmeexp.domain.studyRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.constants.StudyRoomConstants;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationTimeResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyRoomDetailResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.TimeSlotResponse;
import org.example.hugmeexp.domain.studyRoom.service.KakaoMapService;
import org.example.hugmeexp.domain.studyRoom.service.StudyHallSearchService;
import org.example.hugmeexp.domain.studyRoom.service.StudyHallService;
import org.example.hugmeexp.domain.studyRoom.service.StudyRoomService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "StudyRoom - Search & Map", description = "Redis 기반 고성능 스터디룸 검색 및 지도 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studyroom")
public class StudyRoomMapController {

    private final StudyHallService studyHallService;
    private final StudyRoomService studyRoomService;
    private final StudyHallSearchService studyHallSearchService;
    private final KakaoMapService kakaoMapService;

    @Operation(summary = "Redis Geo 기반 주변 검색",
            description = "Redis Geo를 활용한 위치 기반 검색")
    @PostMapping("/search/nearby-redis")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchNearbyWithRedis(
            @Valid @RequestBody StudyHallSearchRequest request) {

        if (!request.hasValidLocationInfo()) {
            return ResponseEntity.badRequest()
                    .body(Response.<List<StudyHallLocationResponse>>builder()
                            .message("유효한 위치 정보가 필요합니다.")
                            .data(List.of())
                            .build());
        }

        List<StudyHallLocationResponse> results = studyHallSearchService
                .searchNearbyStudyHallsWithRedis(request);

        String message = String.format(StudyRoomConstants.SEARCH_RESULT_MESSAGE_FORMAT,
                "Redis Geo 검색", results.size());

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(message)
                .data(results)
                .build());
    }

    @Operation(summary = "Redis Trie 기반 스마트 자동완성",
            description = "Redis Trie 구조를 활용한 자동완성")
    @GetMapping("/search/autocomplete-redis")
    public ResponseEntity<Response<List<String>>> getSmartAutocomplete(
            @Parameter(description = "자동완성 검색어", required = true)
            @RequestParam String query,
            @Parameter(description = "제안 개수", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {

        List<String> suggestions = studyHallSearchService
                .getSmartAutocompleteSuggestions(query, limit);

        String message = String.format("%d개의 자동완성 제안을 찾았습니다.", suggestions.size());

        return ResponseEntity.ok(Response.<List<String>>builder()
                .message(message)
                .data(suggestions)
                .build());
    }

    @Operation(summary = "하이브리드 검색",
            description = "Redis + DB를 조합한 최적화된 검색 (상황에 따라 최적의 방식 자동 선택)")
    @PostMapping("/search/hybrid")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> hybridSearch(
            @Valid @RequestBody StudyHallSearchRequest request) {

        try {
            List<StudyHallLocationResponse> results = studyHallSearchService.hybridSearch(request);

            String message = String.format(StudyRoomConstants.SEARCH_RESULT_MESSAGE_FORMAT,
                    "하이브리드 검색", results.size());

            return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                    .message(message)
                    .data(results)
                    .build());

        } catch (Exception e) {
            log.error("하이브리드 검색 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Response.<List<StudyHallLocationResponse>>builder()
                            .message("검색 중 오류가 발생했습니다.")
                            .data(List.of())
                            .build());
        }
    }

    // === 기존 API (하위 호환성 + Fallback) ===

    @Operation(summary = "모든 스터디홀 위치 조회", description = "지도에 표시할 모든 스터디홀의 위치 정보를 조회합니다.")
    @GetMapping("/map/halls")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> getAllStudyHallsForMap() {
        List<StudyHallLocationResponse> studyHalls = kakaoMapService.getAllStudyHallsForMap();

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("총 %d개의 스터디홀을 조회했습니다.", studyHalls.size()))
                .data(studyHalls)
                .build());
    }

    @Operation(summary = "기본 주변 검색 (Legacy)",
            description = "기존 DB 기반 주변 검색 (Redis 장애시 Fallback용)")
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

        log.info("기본 주변 검색 (DB) - lat: {}, lng: {}, radius: {}km",
                request.getLatitude(), request.getLongitude(), request.getRadius());

        List<StudyHallLocationResponse> nearbyHalls = studyHallSearchService.searchNearbyStudyHalls(request);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format(StudyRoomConstants.SEARCH_RESULT_MESSAGE_FORMAT,
                        "주변 검색", nearbyHalls.size()))
                .data(nearbyHalls)
                .build());
    }

    @Operation(summary = "기본 자동완성 (Legacy)", description = "기존 DB 기반 자동완성 (Redis 장애시 Fallback용)")
    @GetMapping("/search/autocomplete")
    public ResponseEntity<Response<List<String>>> getBasicAutocompleteSuggestions(
            @Parameter(description = "자동완성 검색어", required = true)
            @RequestParam String query,
            @Parameter(description = "제안 개수", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {

        List<String> suggestions = studyHallSearchService.getSmartAutocompleteSuggestions(query, limit);

        return ResponseEntity.ok(Response.<List<String>>builder()
                .message(String.format("%d개의 자동완성 제안을 찾았습니다.", suggestions.size()))
                .data(suggestions)
                .build());
    }

    // === 🏢 스터디홀 상세 정보 API (기존 유지) ===

    @Operation(summary = "특정 스터디홀 상세 정보 조회")
    @GetMapping("/halls/{studyHallId}")
    public ResponseEntity<Response<StudyHallLocationResponse>> getStudyHallDetail(
            @PathVariable Long studyHallId) {

        try {
            StudyHallLocationResponse studyHall = kakaoMapService.getStudyHallDetail(studyHallId);
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

    @Operation(summary = "거리 계산")
    @GetMapping("/halls/{studyHallId}/distance")
    public ResponseEntity<Response<StudyHallLocationResponse>> getStudyHallWithDistance(
            @PathVariable Long studyHallId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        try {
            StudyHallLocationResponse studyHall = kakaoMapService.getStudyHallWithDistance(
                    studyHallId, latitude, longitude);
            return ResponseEntity.ok(Response.<StudyHallLocationResponse>builder()
                    .message("스터디홀 정보와 거리를 계산했습니다.")
                    .data(studyHall)
                    .build());
        } catch (Exception e) {
            log.error("거리 계산 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(Response.<StudyHallLocationResponse>builder()
                            .message("거리 계산에 실패했습니다.")
                            .build());
        }
    }

    // === 🏠 스터디룸 관련 API (기존 유지) ===

    @Operation(summary = "특정 스터디홀의 모든 스터디룸 조회")
    @GetMapping("/halls/{studyHallId}/rooms")
    public ResponseEntity<Response<List<StudyRoomDetailResponse>>> getStudyRoomsInHall(
            @PathVariable Long studyHallId) {

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

    @Operation(summary = "특정 스터디룸 상세 정보 조회")
    @GetMapping("/rooms/{studyRoomId}")
    public ResponseEntity<Response<StudyRoomDetailResponse>> getStudyRoomDetail(
            @PathVariable Long studyRoomId) {

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

    @Operation(summary = "예약 가능한 시간 조회")
    @GetMapping("/rooms/{studyRoomId}/available-times")
    public ResponseEntity<Response<List<TimeSlotResponse>>> getAvailableTimeSlots(
            @PathVariable Long studyRoomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        try {
            List<TimeSlotResponse> availableSlots = studyRoomService.getAvailableTimeSlots(studyRoomId, date);
            return ResponseEntity.ok(Response.<List<TimeSlotResponse>>builder()
                    .message("예약 가능한 시간대를 조회했습니다.")
                    .data(availableSlots)
                    .build());
        } catch (Exception e) {
            log.error("예약 가능 시간 조회 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(Response.<List<TimeSlotResponse>>builder()
                            .message("예약 가능 시간 조회에 실패했습니다.")
                            .data(List.of())
                            .build());
        }
    }

    @Operation(summary = "예약 현황 조회")
    @GetMapping("/rooms/{studyRoomId}/reservations")
    public ResponseEntity<Response<List<ReservationTimeResponse>>> getReservationsByDate(
            @PathVariable Long studyRoomId,
            @RequestParam String date) {

        try {
            LocalDate.parse(date); // 날짜 형식 검증

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
            log.error("예약 현황 조회 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(Response.<List<ReservationTimeResponse>>builder()
                            .message("예약 현황 조회에 실패했습니다.")
                            .data(List.of())
                            .build());
        }
    }

    // === Legacy API (하위 호환성) ===

    @Operation(summary = "주소로 스터디홀 검색 (Legacy)")
    @GetMapping("/search/address")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHallsByAddress(
            @RequestParam String address) {

        List<StudyHallLocationResponse> studyHalls = studyHallSearchService.searchStudyHallsByAddress(address);
        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("주소 '%s'로 %d개의 스터디홀을 찾았습니다.", address, studyHalls.size()))
                .data(studyHalls)
                .build());
    }

    @Operation(summary = "이름으로 스터디홀 검색 (Legacy)")
    @GetMapping("/search/name")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHallsByName(
            @RequestParam String name) {

        List<StudyHallLocationResponse> studyHalls = studyHallSearchService.searchStudyHallsByName(name);
        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("이름 '%s'로 %d개의 스터디홀을 찾았습니다.", name, studyHalls.size()))
                .data(studyHalls)
                .build());
    }
}

