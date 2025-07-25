package org.example.hugmeexp.domain.studyRoom.repository;

import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    // 특정 스터디 홀에 속한 모든 룸을 찾는 메서드.
    List<StudyRoom> findAllByStudyHall(StudyHall studyHall);

}