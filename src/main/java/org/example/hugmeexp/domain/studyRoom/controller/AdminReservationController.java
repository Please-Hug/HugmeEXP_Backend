package org.example.hugmeexp.domain.studyRoom.controller;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.studyRoom.dto.response.ReservationListDto;
import org.example.hugmeexp.domain.studyRoom.service.AdminReservationService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 관리자의 예약 관리 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reservations")
@Tag(name = "Admin - Reservation", description = "관리자 예약 관리 API")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;


    /**
     * 시스템에 등록된 모든 예약 내역을 페이징하여 조회합니다.
     * @param pageable 페이징 정보
     * @return 페이징된 예약 목록과 HTTP 200 OK 상태 코드
     */
    @Operation(summary = "전체 예약 현황 조회", description = "시스템의 모든 예약 내역을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Response<Page<ReservationListDto>>> getAllReservations(Pageable pageable) {
        Page<ReservationListDto> reservations = adminReservationService.getAllReservations(pageable);
        Response<Page<ReservationListDto>> response = Response.<Page<ReservationListDto>>builder()
                .message("전체 예약 목록을 조회했습니다.")
                .data(reservations)
                .build();
        return ResponseEntity.ok(response);
    }


    /**
     * ID로 특정 예약을 강제로 취소합니다.
     * @param reservationId 취소할 예약의 ID
     * @return 성공 메시지와 HTTP 200 OK 상태 코드
     */
    @Operation(summary = "특정 예약 강제 취소", description = "관리자가 특정 예약을 강제로 취소(삭제)합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Response<Void>> forceCancelReservation(@PathVariable Long reservationId) {
        adminReservationService.forceCancelReservation(reservationId);
        Response<Void> response = Response.<Void>builder()
                .message("예약이 성공적으로 취소되었습니다.")
                .build();
        return ResponseEntity.ok(response);
    }
}
