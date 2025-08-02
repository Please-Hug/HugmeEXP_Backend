package org.example.hugmeexp.domain.studyRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallSearchRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallLocationResponse;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.example.hugmeexp.domain.studyRoom.service.StudyHallService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "StudyRoom", description = "스터디룸 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studyroom")
public class StudyRoomController {

    private final StudyHallService studyHallService;
    private final StudyHallRepository studyHallRepository;

    @Operation(summary = "모든 스터디홀 위치 조회", description = "지도에 표시할 모든 스터디홀의 위치 정보를 조회합니다.")
    @GetMapping("/map/halls")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> getAllStudyHallsForMap() {
        List<StudyHallLocationResponse> studyHalls = studyHallService.getAllStudyHallsForMap();

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message("모든 스터디홀 위치 정보를 조회했습니다.")
                .data(studyHalls)
                .build());
    }

    @Operation(summary = "현재 위치 기반 주변 스터디홀 검색",
            description = "현재 위치를 기준으로 지정된 반경 내의 스터디홀을 거리 순으로 조회합니다.")
    @PostMapping("/map/nearby")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchNearbyStudyHalls(
            @Valid @RequestBody StudyHallSearchRequest request) {

        log.info("Nearby study halls search requested - lat: {}, lng: {}, radius: {}km",
                request.getLatitude(), request.getLongitude(), request.getRadius());

        List<StudyHallLocationResponse> nearbyHalls = studyHallService.searchNearbyStudyHalls(request);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("반경 %.1fkm 내 스터디홀 %d개를 찾았습니다.",
                        request.getRadius(), nearbyHalls.size()))
                .data(nearbyHalls)
                .build());
    }

    @Operation(summary = "특정 스터디홀 상세 정보 조회",
            description = "특정 스터디홀의 상세 정보를 조회합니다.")
    @GetMapping("/halls/{studyHallId}")
    public ResponseEntity<Response<StudyHallLocationResponse>> getStudyHallDetail(
            @Parameter(description = "스터디홀 ID", required = true) @PathVariable Long studyHallId) {

        StudyHallLocationResponse studyHall = studyHallService.getStudyHallDetail(studyHallId);

        return ResponseEntity.ok(Response.<StudyHallLocationResponse>builder()
                .message("스터디홀 상세 정보를 조회했습니다.")
                .data(studyHall)
                .build());
    }

    @Operation(summary = "현재 위치로부터 특정 스터디홀까지의 거리 계산",
            description = "현재 위치를 기준으로 특정 스터디홀까지의 거리를 계산하여 반환합니다.")
    @GetMapping("/halls/{studyHallId}/distance")
    public ResponseEntity<Response<StudyHallLocationResponse>> getStudyHallWithDistance(
            @Parameter(description = "스터디홀 ID", required = true) @PathVariable Long studyHallId,
            @Parameter(description = "현재 위치 위도", required = true) @RequestParam Double latitude,
            @Parameter(description = "현재 위치 경도", required = true) @RequestParam Double longitude) {

        StudyHallLocationResponse studyHall = studyHallService.getStudyHallWithDistance(
                studyHallId, latitude, longitude);

        return ResponseEntity.ok(Response.<StudyHallLocationResponse>builder()
                .message("스터디홀 정보와 거리를 계산했습니다.")
                .data(studyHall)
                .build());
    }

    @Operation(summary = "주소로 스터디홀 검색",
            description = "주소를 기준으로 스터디홀을 검색합니다.")
    @GetMapping("/search/address")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHallsByAddress(
            @Parameter(description = "검색할 주소", required = true) @RequestParam String address) {

        List<StudyHallLocationResponse> studyHalls = studyHallService.searchStudyHallsByAddress(address);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("주소 '%s'로 %d개의 스터디홀을 찾았습니다.", address, studyHalls.size()))
                .data(studyHalls)
                .build());
    }

    @Operation(summary = "이름으로 스터디홀 검색",
            description = "이름을 기준으로 스터디홀을 검색합니다.")
    @GetMapping("/search/name")
    public ResponseEntity<Response<List<StudyHallLocationResponse>>> searchStudyHallsByName(
            @Parameter(description = "검색할 스터디홀 이름", required = true) @RequestParam String name) {

        List<StudyHallLocationResponse> studyHalls = studyHallService.searchStudyHallsByName(name);

        return ResponseEntity.ok(Response.<List<StudyHallLocationResponse>>builder()
                .message(String.format("이름 '%s'로 %d개의 스터디홀을 찾았습니다.", name, studyHalls.size()))
                .data(studyHalls)
                .build());
    }
}