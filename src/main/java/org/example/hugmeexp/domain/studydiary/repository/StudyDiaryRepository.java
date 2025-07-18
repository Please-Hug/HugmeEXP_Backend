package org.example.hugmeexp.domain.studydiary.repository;

import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiarySearchResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyDiaryRepository extends JpaRepository<StudyDiary, Long> {


    // 최적화된 검색 쿼리 - Full-text index 사용
    @Query(
        value = "SELECT s.studydiary_id as id, u.name as name, s.title as title, " +
                "SUBSTRING(s.content, 1, 200) as contentPreview, s.like_count as likeNum, " +
                "(SELECT COUNT(*) FROM study_diary_comment c WHERE c.studydiary_id = s.studydiary_id) as commentNum, " +
                "s.created_at as createdAt " +
                "FROM study_diary s JOIN users u ON s.user_id = u.id " +
                "WHERE s.is_created = true AND MATCH(s.title, s.content) AGAINST(:keyword IN BOOLEAN MODE) " +
                "ORDER BY s.created_at DESC",
        countQuery = "SELECT COUNT(*) FROM study_diary s WHERE s.is_created = true AND MATCH(s.title, s.content) AGAINST(:keyword IN BOOLEAN MODE)",
        nativeQuery = true
    )
    Page<StudyDiarySearchResponse> searchOptimized(@Param("keyword") String keyword, Pageable pageable);

    // 최신순 정렬 조회 (페이징) - User fetch join + Comment COUNT
    @Query("SELECT s.id as id, s.user as user, s.title as title, s.content as content, " +
           "s.likeCount as likeCount, COUNT(c.id) as commentCount, s.createdAt as createdAt " +
           "FROM StudyDiary s LEFT JOIN s.user LEFT JOIN s.comments c " +
           "WHERE s.isCreated = true " +
           "GROUP BY s.id, s.user.id, s.user.name, s.title, s.content, s.likeCount, s.createdAt " +
           "ORDER BY s.createdAt DESC")
    Page<Object[]> findByIsCreatedTrueOrderByCreatedAtDesc(Pageable pageable);

    // 임시저장 목록 조회 (페이징)
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.isCreated = false")
    List<StudyDiary> findByIsCreatedFalse(@Param("userId") Long userId);

    // 특정 사용자의 이번 주 작성한 일기 조회
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startOfWeek AND :endOfWeek")
    List<StudyDiary> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                    @Param("startOfWeek") LocalDateTime startOfWeek, 
                                                    @Param("endOfWeek") LocalDateTime endOfWeek);

    // 사용자의 전체 일기 개수
    long countByUser_Id(Long userId);

    // 사용자가 쓴 전체일기 조회 - User fetch join + Comment COUNT
    @Query("SELECT s.id as id, s.user as user, s.title as title, s.content as content, " +
           "s.likeCount as likeCount, COUNT(c.id) as commentCount, s.createdAt as createdAt " +
           "FROM StudyDiary s LEFT JOIN s.user LEFT JOIN s.comments c " +
           "WHERE s.user.id = :findUserId " +
           "GROUP BY s.id, s.user.id, s.user.name, s.title, s.content, s.likeCount, s.createdAt " +
           "ORDER BY s.createdAt DESC")
    List<Object[]> findByUser(@Param("findUserId") Long findUserId);

    // 오늘 하루 인기 일기 조회 (오늘 작성된 글 중 좋아요 많은 순) - User fetch join + Comment COUNT
    @Query("SELECT s.id as id, s.user as user, s.title as title, s.content as content, " +
           "s.likeCount as likeCount, COUNT(c.id) as commentCount, s.createdAt as createdAt " +
           "FROM StudyDiary s LEFT JOIN s.user LEFT JOIN s.comments c " +
           "WHERE s.isCreated = true AND s.createdAt BETWEEN :startOfDay AND :endOfDay " +
           "GROUP BY s.id, s.user.id, s.user.name, s.title, s.content, s.likeCount, s.createdAt " +
           "ORDER BY s.likeCount DESC")
    Page<Object[]> findTodayPopularStudyDiaries(@Param("startOfDay") LocalDateTime startOfDay,
                                                  @Param("endOfDay") LocalDateTime endOfDay, 
                                                  Pageable pageable);

    // 이번 주 인기 일기 조회 (이번 주 작성된 글 중 좋아요 많은 순)
    @Query("SELECT s FROM StudyDiary s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.comments WHERE s.isCreated = true AND s.createdAt BETWEEN :startOfWeek AND :endOfWeek ORDER BY s.likeCount DESC LIMIT 50")
    List<StudyDiary> findWeeklyPopularStudyDiaries(@Param("startOfWeek") LocalDateTime startOfWeek,
                                                   @Param("endOfWeek") LocalDateTime endOfWeek);

    // 최근 한달간 특정 사용자의 배움일기 조회 (생성일 내림차순)
    @Query("SELECT s FROM StudyDiary s WHERE s.user.id = :userId AND s.isCreated = true AND s.createdAt >= :oneMonthAgo ORDER BY s.createdAt DESC")
    Page<StudyDiary> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(@Param("userId") Long userId, 
                                                                      @Param("oneMonthAgo") LocalDateTime oneMonthAgo, 
                                                                      Pageable pageable);

    // 상세 조회용 - User fetch join (Comments는 BatchSize 활용)
    @Query("SELECT s FROM StudyDiary s LEFT JOIN FETCH s.user WHERE s.id = :id")
    Optional<StudyDiary> findByIdWithUser(@Param("id") Long id);
}