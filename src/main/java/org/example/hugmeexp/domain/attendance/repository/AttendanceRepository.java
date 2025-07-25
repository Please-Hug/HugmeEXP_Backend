package org.example.hugmeexp.domain.attendance.repository;

import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 주간/범위 조회
    List<Attendance> findByUser_UsernameAndAttendanceDateBetween(String username, LocalDate startDate, LocalDate endDate);

    // 연속 출석일 계산용, 메서드명 중간에 _ 있어야 user.username 경로를 jpa가 해석 가능
    boolean existsByUser_UsernameAndAttendanceDate(String username, LocalDate attendanceDate);

    // 한 유저가 지금까지 출석한 날짜들만 리스트 조회
    @Query("SELECT a.attendanceDate FROM Attendance a WHERE a.user.username = :username ORDER BY a.attendanceDate ASC")
    List<LocalDate> findDatesByUsername(@Param("username") String username);

    /**
     * 특정 기간 내 출석한 고유 사용자 수 조회 (활성 사용자 통계용)
     * @param startDate 시작 날짜 (포함)
     * @param endDate 종료 날짜 (포함)
     * @return 해당 기간 내 출석한 고유 사용자 수
     */
    @Query("SELECT COUNT(DISTINCT a.user.id) FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate")
    long countDistinctUsersByAttendanceDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}