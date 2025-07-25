package org.example.hugmeexp.domain.user.repository;

import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // username이 존재하는지 확인
    boolean existsByUsername(String username);

    // phoneNumber가 존재하는지 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // username을 바탕으로 User 리턴
    Optional<User> findByUsername(String username);

    // username을 바탕으로 User 삭제
    Integer deleteByUsername(String username);

    // name을 바탕으로 모든 User 리턴
    List<User> findByNameContaining(String name);

    // phoneNumber를 바탕으로 User 리턴
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u JOIN FETCH u.userMissionGroupList umg JOIN FETCH umg.missionGroup ORDER BY u.exp DESC")
    List<User> findAllByOrderByExpDesc();

    // admin 도메인에서 월별 가입자 통계를 위한 쿼리
     long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // admin 도메인에서 특정 기간 내 일별 가입자 수를 조회하는 쿼리, 집계함수와 그룹핑의 조합이므로 jpa 사용 x
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count " +
            "FROM User u " +
            "WHERE u.createdAt >= :startDate AND u.createdAt < :endDate " +
            "GROUP BY DATE(u.createdAt) " +
            "ORDER BY DATE(u.createdAt)")
    List<Object[]> findDailyRegistrationStats(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
