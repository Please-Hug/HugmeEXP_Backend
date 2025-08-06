package org.example.hugmeexp.domain.studyRoom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.dto.request.ReservationCreateDto;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationDetailDto;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationListDto;
import org.example.hugmeexp.domain.studyRoom.service.StudyRoomReservationService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studyroom")
public class StudyRoomReservationController {

    private final StudyRoomReservationService studyRoomReservationService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "스터디룸 예약 생성")
    @PostMapping("/reservations")
    public ResponseEntity<Response<Object>> createReservation(
            @Valid @RequestBody ReservationCreateDto createDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long reservationId = studyRoomReservationService.createReservation(createDto, userDetails);
        
        Response<Object> response = Response.builder()
                .message("예약이 성공적으로 생성되었습니다.")
                .data(Map.of("reservationId", reservationId))
                .build();
        
        URI location = URI.create(String.format("/api/v1/studyroom/reservations/%d", reservationId));
        return ResponseEntity.created(location).body(response);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "예약 상세 조회")
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<Response<Object>> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReservationDetailDto detailDto = studyRoomReservationService.getReservationDetail(reservationId, userDetails);
        
        Response<Object> response = Response.builder()
                .message("예약 상세 정보를 조회했습니다.")
                .data(detailDto)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "사용자 예약 목록 조회")
    @GetMapping("/reservations")
    public ResponseEntity<Response<Object>> getReservations(
            @PageableDefault(
                    size = 10,
                    page = 0,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        Page<ReservationListDto> reservations = studyRoomReservationService.getReservationList(pageable, userDetails);
        
        Response<Object> response = Response.builder()
                .message("예약 목록을 조회했습니다.")
                .data(reservations)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "예약 취소")
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Response<Object>> deleteReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        studyRoomReservationService.deleteReservation(reservationId, userDetails);
        
        Response<Object> response = Response.builder()
                .message("예약이 성공적으로 취소되었습니다.")
                .data(null)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
