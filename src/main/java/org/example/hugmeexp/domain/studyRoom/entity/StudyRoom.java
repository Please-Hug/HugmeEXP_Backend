package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyroom_id")
    private Long id;

    private String name;

    private Integer maxNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyhall_id")
    private StudyHall studyHall;

    // 스터디 룸 정보 수정 메서드
    public void update(StudyRoomRequest requestDto) {
        Optional.ofNullable(requestDto.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(requestDto.getMaxNum()).ifPresent(maxNum -> this.maxNum = maxNum);
    }
}
