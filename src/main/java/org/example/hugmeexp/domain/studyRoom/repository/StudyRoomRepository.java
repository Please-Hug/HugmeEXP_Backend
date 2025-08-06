package org.example.hugmeexp.domain.studyRoom.repository;

import jakarta.persistence.LockModeType;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    // 부모 홀에 속한, 삭제되지 않은 룸들만 조회
    List<StudyRoom> findAllByStudyHallAndIsDeletedFalse(StudyHall studyHall);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("FROM StudyRoom WHERE id=:id")
    Optional<StudyRoom> findByIdWithLock(Long id);

    // 특정 스터디 홀(ID 기준)에 속한 모든 룸을 찾는 메서드.
    List<StudyRoom> findAllByStudyHallId(Long studyHallId);

    // ID로 삭제되지 않은 특정 룸만 조회
    Optional<StudyRoom> findByIdAndIsDeletedFalse(Long roomId);

}
