package org.example.hugmeexp.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.admin.dto.response.ActiveUsersStatsResponse;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAttendanceService {

    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<LocalDate> getAllAttendanceDatesByAdmin(String username) {
        return attendanceService.getAllAttendanceDates(username);
    }

    /**
     * 활성 사용자 통계 조회
     * 최근 30일 내 출석한 사용자를 활성 사용자로 정의
     */
    @Transactional(readOnly = true)
    public ActiveUsersStatsResponse getActiveUsersStats() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        // 전체 사용자 수
        long totalUsers = userRepository.count();

        // 최근 30일 내 출석한 사용자 수 (중복 제거)
        long activeUsers = attendanceRepository.countDistinctUsersByAttendanceDateBetween(
                thirtyDaysAgo, today
        );

        // 활성 사용자 비율 계산
        double activeUserRate = totalUsers > 0 ? (double) activeUsers / totalUsers : 0.0;

        return ActiveUsersStatsResponse.builder()
                .totalUsers((int) totalUsers)
                .activeUsers((int) activeUsers)
                .activeUserRate(activeUserRate)
                .baseDate(today.toString())
                .build();
    }
}