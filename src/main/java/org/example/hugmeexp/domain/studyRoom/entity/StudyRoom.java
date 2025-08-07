package org.example.hugmeexp.domain.studyRoom.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.studyRoom.dto.request.StudyRoomRequest;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.util.Optional;

@Getter
@Entity
@Table(name = "study_room",
        indexes = {
                // 스터디홀별 룸 조회 최적화
                @Index(name = "idx_study_room_by_hall",
                        columnList = "studyhall_id, is_deleted, max_num",
                        unique = false)
        })
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

    private String thumbnail;

    @Builder.Default
    @JsonProperty("isDeleted")
    private boolean isDeleted = false;

    // 스터디 룸 정보 수정 메서드
    public void update(StudyRoomRequest requestDto) {
        Optional.ofNullable(requestDto.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(requestDto.getMaxNum()).ifPresent(maxNum -> this.maxNum = maxNum);
        Optional.ofNullable(requestDto.getThumbnail()).ifPresent(thumbnail -> this.thumbnail = thumbnail);
    }

    public void delete() {
        this.isDeleted = true;
    }
    // optimistic lock을 위해
    @Version
    private Long version;
}
