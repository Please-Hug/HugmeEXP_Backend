package org.example.hugmeexp.domain.studyRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.mapper.StudyHallMapper;
import org.example.hugmeexp.domain.studyRoom.dto.mapper.StudyRoomMapper;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyHallRequest;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyHallResponse;
import org.example.hugmeexp.domain.studyRoom.dto.response.StudyRoomResponse;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.service.StudyHallService;
import org.example.hugmeexp.domain.studyRoom.service.StudyRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 회의실(스터디 홀) 관련 API 요청을 처리하는 컨트롤러입니다.
 * 관리자 전용 엔드포인트를 포함합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/studyhalls")
@Tag(name = "Admin - StudyRoom", description = "스터디룸/홀 관리 API")
public class AdminStudyRoomController {

    private final StudyHallService studyHallService;
    private final StudyHallMapper studyHallMapper;
    private final StudyRoomService studyRoomService;
    private final StudyRoomMapper studyRoomMapper;


    /**
     * 새로운 회의실(스터디 홀)을 생성하는 API 엔드포인트입니다.
     * @param requestDto 회의실 정보가 담긴 요청 Body
     * @return 생성된 리소스의 URI를 담은 Location 헤더와 리소스 전체 정보를 포함한 201 Created 응답
     */
    @Operation(summary = "스터디 홀 생성", description = "관리자가 새로운 스터디 홀(회의 공간)을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "스터디 홀 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping
    public ResponseEntity<StudyHallResponse> createStudyHall(@RequestBody StudyHallRequest requestDto) {
        StudyHall createdStudyHall = studyHallService.createStudyHall(requestDto);
        StudyHallResponse responseDto = studyHallMapper.toResponseDto(createdStudyHall);
        URI location = URI.create(String.format("/api/v1/admin/studyrooms/%d", createdStudyHall.getId()));

        return ResponseEntity.created(location).body(responseDto);
    }

    /**
     * 등록된 모든 스터디 홀의 목록을 조회합니다.
     * @return 스터디 홀 목록과 HTTP 200 OK 상태 코드
     */
    @Operation(summary = "전체 스터디 홀 조회", description = "시스템에 등록된 모든 스터디 홀 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<StudyHallResponse>> getAllStudyHalls() {
        List<StudyHall> studyHalls = studyHallService.findAllStudyHalls();
        List<StudyHallResponse> responseDtos = studyHalls.stream()
                .map(studyHallMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * ID로 특정 스터디 홀의 상세 정보를 조회합니다.
     * @param studyHallId 조회할 스터디 홀의 ID
     * @return 스터디 홀 상세 정보와 HTTP 200 OK 상태 코드
     */
    @Operation(summary = "특정 스터디 홀 조회", description = "ID로 특정 스터디 홀의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디 홀")
    })
    @GetMapping("/{studyHallId}")
    public ResponseEntity<StudyHallResponse> getStudyHallById(@PathVariable Long studyHallId) {
        StudyHall studyHall = studyHallService.findStudyHallById(studyHallId);
        StudyHallResponse responseDto = studyHallMapper.toResponseDto(studyHall);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 특정 스터디 홀에 새로운 룸을 생성합니다.
     * @param studyHallId 룸을 추가할 스터디 홀의 ID
     * @param requestDto 생성할 룸의 정보
     * @return 생성된 룸의 정보와 HTTP 201 Created
     */
    @Operation(summary = "개별 스터디 룸 생성", description = "특정 스터디 홀에 개별 스터디 룸을 추가합니다.")
    @PostMapping("/{studyHallId}/rooms")
    public ResponseEntity<StudyRoomResponse> createStudyRoom(
            @PathVariable Long studyHallId,
            @RequestBody StudyRoomRequest requestDto) {

        StudyRoom createdStudyRoom = studyRoomService.createStudyRoom(studyHallId, requestDto);
        StudyRoomResponse responseDto = studyRoomMapper.toResponseDto(createdStudyRoom);
        URI location = URI.create(String.format("/api/v1/admin/studyhalls/%d/rooms/%d", studyHallId, createdStudyRoom.getId()));

        return ResponseEntity.created(location).body(responseDto);
    }

    /**
     * 특정 스터디 홀에 속한 모든 룸 목록을 조회합니다.
     * @param studyHallId 조회할 스터디 홀의 ID
     * @return 스터디 룸 목록과 HTTP 200 OK
     */
    @Operation(summary = "특정 홀의 룸 목록 조회", description = "특정 스터디 홀에 속한 모든 스터디 룸 목록을 조회합니다.")
    @GetMapping("/{studyHallId}/rooms")
    public ResponseEntity<List<StudyRoomResponse>> getAllRoomsInHall(@PathVariable Long studyHallId) {
        List<StudyRoom> rooms = studyRoomService.findAllRoomsInHall(studyHallId);
        List<StudyRoomResponse> responseDtos = rooms.stream()
                .map(studyRoomMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }
}