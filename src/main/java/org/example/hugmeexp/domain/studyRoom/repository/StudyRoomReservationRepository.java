package org.example.hugmeexp.domain.studyRoom.repository;

import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoomReservation;
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
public interface StudyRoomReservationRepository extends JpaRepository<StudyRoomReservation, Long> {
    
    @Query("SELECT r FROM StudyRoomReservation r " +
           "LEFT JOIN FETCH r.studyRoom sr " +
           "LEFT JOIN FETCH sr.studyHall sh " +
           "WHERE r.user = :findUser")
    Page<StudyRoomReservation> findByUser(@Param("findUser") User findUser, Pageable pageable);
    
    @Query("SELECT r FROM StudyRoomReservation r " +
           "LEFT JOIN FETCH r.studyRoom sr " +
           "LEFT JOIN FETCH sr.studyHall sh " +
           "LEFT JOIN FETCH r.user u " +
           "WHERE r.id = :id")
    Optional<StudyRoomReservation> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT r FROM StudyRoomReservation r " +
           "WHERE r.studyRoom = :studyRoom " +
           "AND r.reservationEnd > :currentTime")
    List<StudyRoomReservation> findAllByStudyRoomAndEndTimeAfter(
            @Param("studyRoom") StudyRoom studyRoom, 
            @Param("currentTime") LocalDateTime currentTime);
    
    Optional<StudyRoomReservation> findByIdAndUser(Long id, User user);

    // 관리자가 모든 예약을 조회할 때 사용할 페이징 쿼리
    @Query(value = "SELECT r FROM StudyRoomReservation r " +
            "JOIN FETCH r.studyRoom sr " +
            "JOIN FETCH sr.studyHall sh",
            countQuery = "SELECT count(r) FROM StudyRoomReservation r")
    Page<StudyRoomReservation> findAllWithDetails(Pageable pageable);
}