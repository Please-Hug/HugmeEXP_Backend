package org.example.hugmeexp.domain.studyRoom.repository;

import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyHallRepository extends JpaRepository<StudyHall, Long> {

    // 삭제되지 않은 홀 목록을 페이징하여 조회하는 메서드
    Page<StudyHall> findAllByIsDeletedFalse(Pageable pageable);

    // 삭제되지 않은 특정 홀만 조회하는 메서드
    Optional<StudyHall> findByIdAndIsDeletedFalse(Long studyHallId);
}