package org.example.hugmeexp.domain.studyRoom.repository;

import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    // 부모 홀에 속한, 삭제되지 않은 룸들만 조회
    List<StudyRoom> findAllByStudyHallAndIsDeletedFalse(StudyHall studyHall);

    // ID로 삭제되지 않은 특정 룸만 조회
    Optional<StudyRoom> findByIdAndIsDeletedFalse(Long roomId);

}