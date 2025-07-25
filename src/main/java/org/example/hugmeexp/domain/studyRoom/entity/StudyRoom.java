package org.example.hugmeexp.domain.studyRoom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="studyroom_id")
    private Long id;

    private String name;

    private Integer maxNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyhall_id")
    private StudyHall studyHall;

    // optimistic lock을 위해
    @Version
    private Long version;
}
